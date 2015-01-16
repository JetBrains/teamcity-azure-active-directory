package org.jetbrains.teamcity.aad;

import jetbrains.buildServer.serverSide.auth.LoginConfiguration;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.SimplePageExtension;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Evgeniy.Koshkin
 */
public class LoginViaAADLoginPageExtension extends SimplePageExtension {
  @NotNull
  private final LoginConfiguration myLoginConfiguration;

  public LoginViaAADLoginPageExtension(@NotNull final PagePlaces pagePlaces,
                                       @NotNull final PluginDescriptor pluginDescriptor,
                                       @NotNull final LoginConfiguration loginConfiguration) {
    super(pagePlaces,
            PlaceId.LOGIN_PAGE,
            LoginViaAADLoginPageExtension.class.getName(),
            pluginDescriptor.getPluginResourcesPath("loginViaAAD.jsp"));
    myLoginConfiguration = loginConfiguration;
    register();
  }

  @Override
  public boolean isAvailable(@NotNull HttpServletRequest request) {
    return !myLoginConfiguration.getConfiguredAuthModules(AADAuthenticationScheme.class).isEmpty();
  }
}
