package org.jetbrains.teamcity.aad;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationProtocol;
import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationResult;
import jetbrains.buildServer.controllers.interceptors.auth.util.HttpAuthUtil;

public abstract class TokenAuthenticator {

	protected static final Logger LOG = Logger.getLogger(AADAuthenticationScheme.class);
	
	public abstract HttpAuthenticationResult processAuthenticationRequest(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException;
	
	protected HttpAuthenticationResult sendUnauthorized(HttpServletRequest request, HttpServletResponse response, String reason) throws IOException {
	   return HttpAuthUtil.sendUnauthorized(request, response, reason, Collections.<HttpAuthenticationProtocol>emptySet());
	}

	protected HttpAuthenticationResult sendBadRequest(HttpServletResponse response, String reason) throws IOException {
	  LOG.warn(reason);
	  response.sendError(HttpStatus.BAD_REQUEST.value(), reason);
	  return HttpAuthenticationResult.unauthenticated();
	}
	
}