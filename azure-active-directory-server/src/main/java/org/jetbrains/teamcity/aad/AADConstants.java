package org.jetbrains.teamcity.aad;

import jetbrains.buildServer.PluginTypes;
import jetbrains.buildServer.users.PluginPropertyKey;
import jetbrains.buildServer.users.PropertyKey;

/**
 * @author Evgeniy.Koshkin
 */
public class AADConstants {
  public static final String AAD_AUTH_SCHEME_NAME = "AAD";
  public static final String AUTH_ENDPOINT_SCHEME_PROPERTY_KEY = "authEndpoint";
  public static final String CLIENT_ID_SCHEME_PROPERTY_KEY = "clientId";
  public static final String DISABLE_LOGIN_FORM = "disableLoginForm"; 
  public static final String ALLOW_MATCHING_USERS_BY_EMAIL = "allowMatchingUsersByEmail";
  public static final String ENABLE_TOKEN_AUTHENTICATION = "enableTokenAuthentication";
  public static final String ID_CLAIM_TOKEN_AUTHENTICATION = "idClaimTokenAuthentication";
  public static final PropertyKey OID_USER_PROPERTY_KEY = new PluginPropertyKey(PluginTypes.AUTH_PLUGIN_TYPE, "azure-active-directory", "oid");
}
