package org.jetbrains.teamcity.aad;

import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationProtocol;
import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationResult;
import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationSchemeAdapter;
import jetbrains.buildServer.controllers.interceptors.auth.util.HttpAuthUtil;
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
    
    if(Boolean.valueOf(properties.get(AADConstants.ENABLE_TOKEN_AUTHENTICATION)) && !Boolean.valueOf(properties.get(AADConstants.ALLOW_MATCHING_USERS_BY_EMAIL))){
      errors.add("In order to enable token authentication, matching users by email must be enabled too.");
    }
    
    return errors.isEmpty() ? super.validate(properties) : errors;
  }

  @NotNull
  @Override
  public HttpAuthenticationResult processAuthenticationRequest(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Map<String, String> schemeProperties) throws IOException {
	
	  final TokenAuthenticator tokenAuthenticator = new TokenAuthenticatorFactory(schemeProperties, myPluginDescriptor, myPrincipalFactory)
			  					  					   .GetTokenAuthenticator(request);
	  
	  if(tokenAuthenticator == null) 
	  {
		  LOG.debug("Request contains no token parameter or Authorization header so scheme is not applicable.");
		  return HttpAuthenticationResult.notApplicable();
	  }
	  
	  return tokenAuthenticator.processAuthenticationRequest(request, response);
  }
}
