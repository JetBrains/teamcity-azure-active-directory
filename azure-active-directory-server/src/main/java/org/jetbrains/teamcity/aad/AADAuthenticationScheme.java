package org.jetbrains.teamcity.aad;

import jetbrains.buildServer.controllers.interceptors.auth.HttpAuthenticationSchemeAdapter;
import jetbrains.buildServer.serverSide.auth.LoginConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * @author Evgeniy.Koshkin
 */
public class AADAuthenticationScheme extends HttpAuthenticationSchemeAdapter {

  public AADAuthenticationScheme(@NotNull final LoginConfiguration loginConfiguration) {
    loginConfiguration.registerAuthModuleType(this);
  }

  @NotNull
  @Override
  protected String doGetName() {
    return "AAD";
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Microsoft Azure Active Directory";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Authentication via Microsoft Azure Active Directory";
  }
}
