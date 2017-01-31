package org.jetbrains.teamcity.aad;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jetbrains.annotations.NotNull;

import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationResult;
import jetbrains.buildServer.serverSide.auth.ServerPrincipal;
import jetbrains.buildServer.web.openapi.PluginDescriptor;

public class BearerTokenAuthenticator extends TokenAuthenticator {
	
	@NotNull private final PluginDescriptor myPluginDescriptor;
	@NotNull private final ServerPrincipalFactory myPrincipalFactory;
	  
	private Map<String, String> schemeProperties;
	
	public BearerTokenAuthenticator(@NotNull Map<String, String> schemeProperties,
            @NotNull final PluginDescriptor pluginDescriptor,
            @NotNull final ServerPrincipalFactory principalFactory) {
		this.myPluginDescriptor = pluginDescriptor;
		this.myPrincipalFactory = principalFactory;
		this.schemeProperties = schemeProperties;
	}
	
	@NotNull
	@Override
	public HttpAuthenticationResult processAuthenticationRequest(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
		
		final String claimName = schemeProperties.get(AADConstants.ID_CLAIM_TOKEN_AUTHENTICATION);
		final String idTokenString = this.GetToken(request);
	
	    final JWT token = JWT.parse(idTokenString);
	    if(token == null)
	      return sendBadRequest(response, String.format("Marked request as unauthenticated since failed to parse JWT from retrieved %s", idTokenString));
	
	    final String error = token.getClaim(ClaimsConstants.ERROR_CLAIM);
	    final String errorDescription = token.getClaim(ClaimsConstants.ERROR_DESCRIPTION_CLAIM);
	
	    if(error != null){
	      LOG.warn(error);
	      return sendUnauthorized(request, response, errorDescription);
	    }
	
	    final String oid = token.getClaim(claimName);
	
	    if (oid == null)
	      return sendBadRequest(response, "The required claim '" + claimName + "' was not found in parsed JWT");
	
	    final ServerPrincipal principal = myPrincipalFactory.getServerPrincipal(oid, oid, oid, schemeProperties);
	   
	    if(principal == null){
			return sendUnauthorized(request, response, String.format("User with " + claimName + " %s not found. (Remember to place the '" + claimName + "' in the email field)", oid));
	    }
	    LOG.debug("Request authenticated. Determined user " + principal.getName());
	    return HttpAuthenticationResult.authenticated(principal, true);
	}
	
	private String GetToken(HttpServletRequest request)
	{
		final String header = request.getHeader("authorization");
		if(header == null)
			return null;
		
		return header.substring(7);
	}
}
