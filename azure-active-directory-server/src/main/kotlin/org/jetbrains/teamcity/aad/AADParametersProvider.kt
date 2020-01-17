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
