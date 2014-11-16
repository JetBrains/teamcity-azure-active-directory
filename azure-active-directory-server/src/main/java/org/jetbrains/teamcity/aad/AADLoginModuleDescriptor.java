package org.jetbrains.teamcity.aad;

import jetbrains.buildServer.serverSide.auth.LoginConfiguration;
import jetbrains.buildServer.serverSide.auth.LoginModuleDescriptorAdapter;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.spi.LoginModule;

/**
 * @author Evgeniy.Koshkin
 */
public class AADLoginModuleDescriptor extends LoginModuleDescriptorAdapter {

  public AADLoginModuleDescriptor(LoginConfiguration loginConfiguration) {
    loginConfiguration.registerAuthModuleType(this);
  }

  @NotNull
  public String getName() {
    return "aad";
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

  public Class<? extends LoginModule> getLoginModuleClass() {
    return AADLoginModule.class;
  }
}
