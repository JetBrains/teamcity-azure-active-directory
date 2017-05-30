package org.jetbrains.teamcity.aad;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jetbrains.annotations.NotNull;

import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationResult;
import jetbrains.buildServer.serverSide.auth.ServerPrincipal;
import jetbrains.buildServer.web.openapi.PluginDescriptor;

public class AADLoginTokenAuthenticatior extends TokenAuthenticator {

	private static final String POST_METHOD = "POST";
	private static final String ID_TOKEN = "id_token";

	@NotNull private final PluginDescriptor myPluginDescriptor;
	@NotNull private final ServerPrincipalFactory myPrincipalFactory;
	  
	private Map<String, String> schemeProperties;
	
	public AADLoginTokenAuthenticatior(@NotNull Map<String, String> schemeProperties,
            @NotNull final PluginDescriptor pluginDescriptor,
            @NotNull final ServerPrincipalFactory principalFactory) {
		this.myPluginDescriptor = pluginDescriptor;
		this.myPrincipalFactory = principalFactory;
		this.schemeProperties = schemeProperties;
	}
	
	@Override
	public HttpAuthenticationResult processAuthenticationRequest(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
			
		final String idTokenString = this.GetToken(request);
	
	    final JWT token = JWT.parse(idTokenString);
	    if(token == null)
	      return sendBadRequest(response, String.format("Marked request as unauthenticated since failed to parse JWT from retrieved %s %s", ID_TOKEN, idTokenString));
	
	    final String error = token.getClaim(ClaimsConstants.ERROR_CLAIM);
	    final String errorDescription = token.getClaim(ClaimsConstants.ERROR_DESCRIPTION_CLAIM);
	
	    if(error != null) {
	      LOG.warn(error);
	      return sendUnauthorized(request, response, errorDescription);
	    }
	
	    final String nonce = token.getClaim(ClaimsConstants.NONCE_CLAIM);
	    final String name = token.getClaim(ClaimsConstants.NAME_CLAIM);
	    final String oid = token.getClaim(ClaimsConstants.OID_CLAIM);
	
	    if (nonce == null || name == null || oid == null)
	      return sendBadRequest(response, String.format("The nonce, name and oid claims are required for Token authentication. Parsed values: nonce - %s; name - %s, oid - %s", nonce, name, oid));
	
	    if(!nonce.equals(SessionUtil.getSessionId(request)))
	      return sendBadRequest(response, "Marked request as unauthenticated since retrieved JWT 'nonce' claim doesn't correspond to the current TeamCity session.");
	
	    final String email = token.getClaim(ClaimsConstants.EMAIL_CLAIM);
	
	    final ServerPrincipal principal = myPrincipalFactory.getServerPrincipal(name, oid, email, schemeProperties);
	   
	    if(principal == null) {
			return sendUnauthorized(request, response, String.format("User not found for %s: %s", email != null? "email" : "name", email != null? email : name));
	    }
	    LOG.debug("Request authenticated. Determined user " + principal.getName());
	    return HttpAuthenticationResult.authenticated(principal, true);
	}

	private String GetToken(HttpServletRequest request)
	{
		if (!request.getMethod().equals(POST_METHOD)) 
			return null;
	
		return request.getParameter(ID_TOKEN);
	}
}
