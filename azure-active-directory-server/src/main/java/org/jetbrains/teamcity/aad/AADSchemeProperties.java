package org.jetbrains.teamcity.aad;

import jetbrains.buildServer.serverSide.auth.AuthModule;
import jetbrains.buildServer.serverSide.auth.LoginConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author Evgeniy.Koshkin
 */
public class AADSchemeProperties {

  @NotNull private final LoginConfiguration myLoginConfiguration;

  public AADSchemeProperties(@NotNull final LoginConfiguration loginConfiguration) {
    myLoginConfiguration = loginConfiguration;
  }

  @Nullable
  public String getAppOAuthEndpoint() {
    final Map<String, String> properties = getAADSchemeProperties();
    return properties == null ? null : properties.get(AADSchemePropertiesKeys.AUTH_ENDPOINT_KEY);
  }

  @Nullable
  public String getClientId() {
    final Map<String, String> properties = getAADSchemeProperties();
    return properties == null ? null : properties.get(AADSchemePropertiesKeys.CLIENT_ID_KEY);
  }

  public boolean isSchemeConfigured() {
    return !myLoginConfiguration.getConfiguredAuthModules(AADAuthenticationScheme.class).isEmpty();
  }

  @Nullable
  private Map<String, String> getAADSchemeProperties() {
    final List<AuthModule<AADAuthenticationScheme>> aadAuthModules = myLoginConfiguration.getConfiguredAuthModules(AADAuthenticationScheme.class);
    return aadAuthModules.isEmpty() ? null : aadAuthModules.get(0).getProperties();
  }
}
