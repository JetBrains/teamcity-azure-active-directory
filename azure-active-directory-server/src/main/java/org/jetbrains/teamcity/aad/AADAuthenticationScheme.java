package org.jetbrains.teamcity.aad;

import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationProtocol;
import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationResult;
import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationSchemeAdapter;
import jetbrains.buildServer.controllers.interceptors.auth.util.HttpAuthUtil;
import jetbrains.buildServer.serverSide.auth.AuthModuleUtil;
import jetbrains.buildServer.serverSide.auth.LoginConfiguration;
import jetbrains.buildServer.serverSide.auth.ServerPrincipal;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author Evgeniy.Koshkin
 */
public class AADAuthenticationScheme extends HttpAuthenticationSchemeAdapter {

  private static final Logger LOG = Logger.getLogger(AADAuthenticationScheme.class);

  private static final String POST_METHOD = "POST";
  private static final String ID_TOKEN = "id_token";
  private static final String NONCE_CLAIM = "nonce";
  private static final String NAME_CLAIM = "unique_name";
  private static final String OID_CLAIM = "oid"; //object ID
  private static final String EMAIL_CLAIM = "upn";
  private static final String ERROR_CLAIM = "error";
  private static final String ERROR_DESCRIPTION_CLAIM = "error_description";

  @NotNull private final PluginDescriptor myPluginDescriptor;
  @NotNull private final ServerPrincipalFactory myPrincipalFactory;

  public AADAuthenticationScheme(@NotNull final LoginConfiguration loginConfiguration,
                                 @NotNull final PluginDescriptor pluginDescriptor,
                                 @NotNull final ServerPrincipalFactory principalFactory) {
    myPluginDescriptor = pluginDescriptor;
    myPrincipalFactory = principalFactory;
    loginConfiguration.registerAuthModuleType(this);
  }

  @NotNull
  @Override
  protected String doGetName() {
    return AADConstants.AAD_AUTH_SCHEME_NAME;
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Microsoft Azure Active Directory";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Authentication via Microsoft Azure Active Directory";
  }

  @Nullable
  @Override
  public String getEditPropertiesJspFilePath() {
    return myPluginDescriptor.getPluginResourcesPath("editAADSchemeProperties.jsp");
  }

  @Nullable
  @Override
  public Collection<String> validate(@NotNull Map<String, String> properties) {
    final Collection<String> errors = new ArrayList<String>();
    if(StringUtil.isEmptyOrSpaces(properties.get(AADConstants.AUTH_ENDPOINT_SCHEME_PROPERTY_KEY))){
      errors.add("App OAuth 2.0 authorization endpoint should be specified.");
    }
    if(StringUtil.isEmptyOrSpaces(properties.get(AADConstants.CLIENT_ID_SCHEME_PROPERTY_KEY))){
      errors.add("Client ID should be specified.");
    }
    return errors.isEmpty() ? super.validate(properties) : errors;
  }

  @NotNull
  @Override
  public HttpAuthenticationResult processAuthenticationRequest(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Map<String, String> schemeProperties) throws IOException {
	
	final String idTokenString = this.GetToken(request, schemeProperties);

	if(idTokenString == null || idTokenString.isEmpty()){
	  LOG.debug("Request contains no " + ID_TOKEN + " parameter or Authorization header so scheme is not applicable.");
	  return HttpAuthenticationResult.notApplicable();
	}

    final JWT token = JWT.parse(idTokenString);
    if(token == null)
      return sendBadRequest(response, String.format("Marked request as unauthenticated since failed to parse JWT from retrieved %s %s", ID_TOKEN, idTokenString));

    final String error = token.getClaim(ERROR_CLAIM);
    final String errorDescription = token.getClaim(ERROR_DESCRIPTION_CLAIM);

    if(error != null){
      LOG.warn(error);
      return sendUnauthorized(request, response, errorDescription);
    }

    final String nonce = token.getClaim(NONCE_CLAIM);
    final String name = token.getClaim(NAME_CLAIM);
    final String oid = token.getClaim(OID_CLAIM);

    if (nonce == null || name == null || oid == null)
      return sendBadRequest(response, String.format("Some of required claims were not found in parsed JWT. nonce - %s; name - %s, oid - %s", nonce, name, oid));

    if(!nonce.equals(SessionUtil.getSessionId(request)))
      return sendBadRequest(response, "Marked request as unauthenticated since retrieved JWT 'nonce' claim doesn't correspond to current TeamCity session.");

    final String email = token.getClaim(EMAIL_CLAIM);

    final ServerPrincipal principal = myPrincipalFactory.getServerPrincipal(name, oid, email, schemeProperties);
   
    if(principal == null)
	{
		return sendUnauthorized(request, response, String.format("User not found for %s: %s", email != null? "email" : "name", email != null? email : name));
	}
    
    LOG.debug("Request authenticated. Determined user " + principal.getName());
    return HttpAuthenticationResult.authenticated(principal, true);
  }

  private String GetToken(HttpServletRequest request, @NotNull Map<String, String> schemeProperties)
  {
	if(Boolean.valueOf(schemeProperties.getOrDefault(AADConstants.ENABLE_TOKEN_AUTHENTICATION, "false")))
	{
		final String token = this.FindIdTokenInHeader(request);
		if(token != null && !token.isEmpty())
			return token;
	}
	
	return this.FindIdTokenInParameters(request);
  }
  
  private String FindIdTokenInHeader(HttpServletRequest request)
  {
	final String header = request.getHeader("authorization");
	if(header == null)
		return null;
	
	return header.substring(7);
  }
  
  private String FindIdTokenInParameters(HttpServletRequest request)
  {
	if (!request.getMethod().equals(POST_METHOD)) 
		return null;
	
	return request.getParameter(ID_TOKEN);	 
  }
  
  private HttpAuthenticationResult sendUnauthorized(HttpServletRequest request, HttpServletResponse response, String reason) throws IOException {
    return HttpAuthUtil.sendUnauthorized(request, response, reason, Collections.<HttpAuthenticationProtocol>emptySet());
  }

  private HttpAuthenticationResult sendBadRequest(HttpServletResponse response, String reason) throws IOException {
    LOG.warn(reason);
    response.sendError(HttpStatus.BAD_REQUEST.value(), reason);
    return HttpAuthenticationResult.unauthenticated();
  }
}
