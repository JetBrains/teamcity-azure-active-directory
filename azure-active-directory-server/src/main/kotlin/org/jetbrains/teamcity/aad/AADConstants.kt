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

    const val ID_TOKEN = "id_token"
    const val NONCE_CLAIM = "nonce"

    val OID_USER_PROPERTY_KEY: PropertyKey = PluginPropertyKey(PluginTypes.AUTH_PLUGIN_TYPE, "azure-active-directory", "oid")
}
