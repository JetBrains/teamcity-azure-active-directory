package org.jetbrains.teamcity.aad

import jetbrains.buildServer.serverSide.auth.LoginConfiguration

/**
 * @author Evgeniy.Koshkin
 */
class AADSchemeProperties(private val loginConfiguration: LoginConfiguration) {

    val appOAuthEndpoint: String?
        get() = properties?.get(AADConstants.AUTH_ENDPOINT_SCHEME_PROPERTY_KEY)

    val clientId: String?
        get() = properties?.get(AADConstants.CLIENT_ID_SCHEME_PROPERTY_KEY)

    val isSchemeConfigured: Boolean
        get() = properties != null

    private val properties: Map<String, String>?
        get() {
            val aadAuthModules = loginConfiguration.getConfiguredAuthModules<AADAuthenticationScheme>(AADAuthenticationScheme::class.java)
            return if (aadAuthModules.isEmpty()) null else aadAuthModules[0].properties
        }
}
