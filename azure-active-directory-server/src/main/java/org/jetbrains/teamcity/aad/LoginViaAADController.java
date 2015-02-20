package org.jetbrains.teamcity.aad;

import jetbrains.buildServer.controllers.AuthorizationInterceptor;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Evgeniy.Koshkin
 */
public class LoginViaAADController extends BaseController {

  @NotNull public static final String LOGIN_PATH = "/aadLogin.html";
  @NotNull private final AADSchemeProperties myAADSchemeProperties;

  public LoginViaAADController(@NotNull final WebControllerManager webManager,
                               @NotNull final AuthorizationInterceptor authInterceptor,
                               @NotNull final AADSchemeProperties aadSchemeProperties) {
    myAADSchemeProperties = aadSchemeProperties;
    webManager.registerController(LOGIN_PATH, this);
    authInterceptor.addPathNotRequiringAuth(LOGIN_PATH);
  }

  @Nullable
  @Override
  protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
    final String nonce = SessionUtil.getSessionId(request);
    final String appOAuthEndpoint = myAADSchemeProperties.getAppOAuthEndpoint();
    final String clientId = myAADSchemeProperties.getClientId();
    if(appOAuthEndpoint == null || clientId == null) return null;
    final String requestUrl = AADOpenIdConnect.getRequestUrl(appOAuthEndpoint, clientId, nonce);
    return new ModelAndView(new RedirectView(requestUrl));
  }
}
