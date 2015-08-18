package org.jetbrains.teamcity.aad;

import org.jetbrains.annotations.NotNull;

/**
 * @author Evgeniy.Koshkin
 */
public class AADOpenIdConnect {
  @NotNull
  public static String getRequestUrl(@NotNull final String appEndpoint, @NotNull final String clientId, @NotNull final String nonce) {
    return String.format("%s?response_type=id_token&client_id=%s&scope=openid&nonce=%s&response_mode=form_post", appEndpoint, clientId, nonce);
  }
}
