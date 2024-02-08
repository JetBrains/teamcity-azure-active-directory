

package org.jetbrains.teamcity.aad

import jetbrains.buildServer.serverSide.TeamCityProperties

class AADAuthCallbackPathProviderImpl : AADAuthCallbackPathProvider {
    override val path: String
        get() = when(TeamCityProperties.getPropertyOrNull(AADConstants.ENDPOINT_TYPE_PROPERTY)?.toLowerCase()) {
            AADConstants.DEDICATED_ENDPOINT_TYPE -> AADConstants.DEDICATED_AUTH_PATH
            else -> AADConstants.OVERVIEW_PATH
        }
}