package org.jetbrains.teamcity.aad;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Evgeniy.Koshkin
 */
public class SessionUtil {

  //see jetbrains.buildServer.controllers.login.RememberUrl
  private static final String URL_KEY = "URL_KEY";

  @NotNull
  public static String getSessionId(@NotNull final HttpServletRequest request) {
    // we must use requested session id, if it is presented, and only if not, then we can use current session id, see TW-23821
    final String requestedSessionId = request.getRequestedSessionId();
    if (requestedSessionId != null) {
      return requestedSessionId;
    }
    return request.getSession().getId();
  }

  @Nullable
  public static String readAndForgetInitialRequestUrl(@NotNull HttpServletRequest request) {
    String url = (String)request.getSession().getAttribute(URL_KEY);
    request.getSession().removeAttribute(URL_KEY);
    return url;
  }
}
