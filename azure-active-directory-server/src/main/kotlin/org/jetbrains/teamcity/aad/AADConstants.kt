package org.jetbrains.teamcity.aad

import jetbrains.buildServer.PluginTypes
import jetbrains.buildServer.users.PluginPropertyKey
import jetbrains.buildServer.users.PropertyKey

/**
 * @author Evgeniy.Koshkin
 */
object AADConstants {
    const val AAD_AUTH_SCHEME_NAME = "AAD"
    const val AUTH_ENDPOINT_SCHEME_PROPERTY_KEY = "authEndpoint"
    const val CLIENT_ID_SCHEME_PROPERTY_KEY = "clientId"
    const val ALLOW_MATCHING_USERS_BY_EMAIL = "allowMatchingUsersByEmail"
    const val ALLOW_USER_DETAILS_SYNC = "allowUserDetailsSync"
    const val AUTH_PROMPT = "authPrompt"
    const val LOGIN_PATH = "/aadLogin.html"

    val OID_USER_PROPERTY_KEY: PropertyKey = PluginPropertyKey(PluginTypes.AUTH_PLUGIN_TYPE, "azure-active-directory", "oid")
}
