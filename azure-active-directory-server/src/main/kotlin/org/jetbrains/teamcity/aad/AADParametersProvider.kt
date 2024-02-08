

package org.jetbrains.teamcity.aad

class AADParametersProvider {
    val allowUserDetailsSync: String
        get() = AADConstants.ALLOW_USER_DETAILS_SYNC

    val allowMatchingUsersByEmail: String
        get() = AADConstants.ALLOW_MATCHING_USERS_BY_EMAIL

    val authEndpoint: String
        get() = AADConstants.AUTH_ENDPOINT_SCHEME_PROPERTY_KEY

    val applicationId: String
        get() = AADConstants.CLIENT_ID_SCHEME_PROPERTY_KEY

    val authPrompt: String
        get() = AADConstants.AUTH_PROMPT
}