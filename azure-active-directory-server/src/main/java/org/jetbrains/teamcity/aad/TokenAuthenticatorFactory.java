package org.jetbrains.teamcity.aad;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jetbrains.annotations.NotNull;

import jetbrains.buildServer.web.openapi.PluginDescriptor;

public class TokenAuthenticatorFactory {

	private static final String POST_METHOD = "POST";
	private static final String ID_TOKEN = "id_token";
	
	@NotNull private final Map<String, String> schemeProperties;
	@NotNull private final PluginDescriptor pluginDescriptor;
	@NotNull private final ServerPrincipalFactory principalFactory;
	
	public TokenAuthenticatorFactory(@NotNull Map<String, String> schemeProperties,
            @NotNull final PluginDescriptor pluginDescriptor,
            @NotNull final ServerPrincipalFactory principalFactory) {
		this.pluginDescriptor = pluginDescriptor;
		this.principalFactory = principalFactory;
		this.schemeProperties = schemeProperties;
	}
	
	public TokenAuthenticator GetTokenAuthenticator(@NotNull HttpServletRequest request)
	{
		final String header = request.getHeader("authorization");
		if(header != null)
			return new BearerTokenAuthenticator(schemeProperties, pluginDescriptor, principalFactory);

		if (request.getMethod().equals(POST_METHOD) && request.getParameter(ID_TOKEN) != null) 
			return new AADLoginTokenAuthenticatior(schemeProperties, pluginDescriptor, principalFactory);
		
		return null;
	}
}
