package org.jetbrains.teamcity.aad;

import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.SimplePageExtension;
import org.jetbrains.annotations.NotNull;

/**
 * @author Evgeniy.Koshkin
 */
public class LoginViaAADLoginPageExtension extends SimplePageExtension {
  public LoginViaAADLoginPageExtension(@NotNull final PagePlaces pagePlaces,
                                        @NotNull final PluginDescriptor pluginDescriptor) {
    super(pagePlaces,
            PlaceId.LOGIN_PAGE,
            LoginViaAADLoginPageExtension.class.getName(),
            pluginDescriptor.getPluginResourcesPath("loginViaAAD.jsp"));
    register();
  }
}
