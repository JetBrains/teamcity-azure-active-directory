/*
 * Copyright 2000-2020 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    val authPrompt: String?
        get() = properties?.get(AADConstants.AUTH_PROMPT)

    val isSchemeConfigured: Boolean
        get() = properties != null

    private val properties: Map<String, String>?
        get() {
            val aadAuthModules = loginConfiguration.getConfiguredAuthModules<AADAuthenticationScheme>(AADAuthenticationScheme::class.java)
            return if (aadAuthModules.isEmpty()) null else aadAuthModules[0].properties
        }
}
