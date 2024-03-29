

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
    const val OVERVIEW_PATH = "/overview.html"
    const val DEDICATED_AUTH_PATH = "/aadAuth.html"

    const val ID_TOKEN = "id_token"
    const val NONCE_CLAIM = "nonce"

    val OID_USER_PROPERTY_KEY: PropertyKey = PluginPropertyKey(PluginTypes.AUTH_PLUGIN_TYPE, "azure-active-directory", "oid")

    const val TTL_IN_MINUTES_PROPERTY = "teamcity.aad.token.ttl"

    const val ENDPOINT_TYPE_PROPERTY = "teamcity.aad.endpoint.type"
    const val DEDICATED_ENDPOINT_TYPE = "dedicated"
}