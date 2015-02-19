package org.jetbrains.teamcity.aad;

/**
 * @author Evgeniy.Koshkin
 */
public class AADAppConfig {
  public String getOAuthAuthorizationEndpoint() {
    return "https://login.windows.net/338e019c-f0d6-40a6-9f4d-9c33f4b0fbd3/oauth2/authorize?api-version=1.0";
  }

  public String getClientId() {
    return "d6a5a72d-747c-421f-a741-a90b8fc3e2c6";
  }
}
