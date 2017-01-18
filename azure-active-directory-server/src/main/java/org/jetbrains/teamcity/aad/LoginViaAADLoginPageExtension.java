package org.jetbrains.teamcity.aad;

import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.SimplePageExtension;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Evgeniy.Koshkin
 */
public class LoginViaAADLoginPageExtension extends SimplePageExtension {
  @NotNull
  private final AADSchemeProperties mySchemeProperties;

  public LoginViaAADLoginPageExtension(@NotNull final PagePlaces pagePlaces,
                                       @NotNull final PluginDescriptor pluginDescriptor,
                                       @NotNull final AADSchemeProperties schemeProperties) {
    super(pagePlaces,
            PlaceId.LOGIN_PAGE,
            LoginViaAADLoginPageExtension.class.getName(),
            pluginDescriptor.getPluginResourcesPath("loginViaAAD.jsp"));
    mySchemeProperties = schemeProperties;
    register();
  }

  @Override
  public boolean isAvailable(@NotNull HttpServletRequest request) {
    return mySchemeProperties.isSchemeConfigured();
  }
  
  @Override
  public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
      super.fillModel(model, request);
      model.put("AAD_settings", mySchemeProperties);
  }
}
