package org.jetbrains.teamcity.aad;

import jetbrains.buildServer.PluginTypes;
import jetbrains.buildServer.users.PluginPropertyKey;
import jetbrains.buildServer.users.PropertyKey;

/**
 * @author Evgeniy.Koshkin
 */
public class AADConstants {
  public static final String AAD_AUTH_SCHEME_NAME = "AAD";
  public static final String AAD_TENANT_ID = "directoryId";
  public static final String CLIENT_ID_SCHEME_PROPERTY_KEY = "clientId";
  public static final PropertyKey OID_USER_PROPERTY_KEY = new PluginPropertyKey(PluginTypes.AUTH_PLUGIN_TYPE, "azure-active-directory", "oid");
}
