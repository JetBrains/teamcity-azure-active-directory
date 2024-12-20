

package org.jetbrains.teamcity.aad

import jetbrains.buildServer.serverSide.TeamCityProperties

class AADAuthCallbackPathProviderImpl : AADAuthCallbackPathProvider {
    override val path: String
        get() = when(TeamCityProperties.getProperty(AADConstants.ENDPOINT_TYPE_PROPERTY, AADConstants.DEDICATED_ENDPOINT_TYPE).toLowerCase()) {
            AADConstants.DEDICATED_ENDPOINT_TYPE -> AADConstants.DEDICATED_AUTH_PATH
            else -> AADConstants.OVERVIEW_PATH
        }
}