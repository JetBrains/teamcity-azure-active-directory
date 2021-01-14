/*
 * Copyright 2000-2021 JetBrains s.r.o.
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

import jetbrains.buildServer.serverSide.auth.AuthModuleUtil
import jetbrains.buildServer.serverSide.auth.ServerPrincipal
import jetbrains.buildServer.users.SUser
import jetbrains.buildServer.users.UserModel
import org.apache.log4j.Logger

/**
 * @author Evgeniy.Koshkin
 */
class ServerPrincipalFactory(private val myUserModel: UserModel) {

    fun getServerPrincipal(userName: String, aadUserUID: String, displayName: String?, email: String?, schemeProperties: Map<String, String>): ServerPrincipal {
        // Match by UID
        val userWithTheSameUID = findExistingUserByUID(aadUserUID)
        if (userWithTheSameUID != null) {
            if (allowUserDetailsSync(schemeProperties)) {
                userWithTheSameUID.updateUserAccount(userName, displayName, email)
            }
            return ServerPrincipal(AADConstants.AAD_AUTH_SCHEME_NAME, userWithTheSameUID.username)
        }

        // Match by email
        if (email != null && allowMatchUserByEmail(schemeProperties)) {
            val userWithTheSameEmail = findExistingUserByEmail(email)
            if (userWithTheSameEmail != null) {
                val usernameFound = userWithTheSameEmail.username
                LOG.info("Associated Azure AD user $userName with TeamCity user $usernameFound by e-mail $email")
                userWithTheSameEmail.setUserProperty(AADConstants.OID_USER_PROPERTY_KEY, aadUserUID)
                if (allowUserDetailsSync(schemeProperties)) {
                    userWithTheSameEmail.updateUserAccount(userName, displayName, email)
                }
                return ServerPrincipal(AADConstants.AAD_AUTH_SCHEME_NAME, userWithTheSameEmail.username)
            }
        }

        // Create user and populate with users details
        val allowCreatingNewUsersByLogin = AuthModuleUtil.allowCreatingNewUsersByLogin(schemeProperties, DEFAULT_ALLOW_CREATING_NEW_USERS_BY_LOGIN)
        val userProperties = hashMapOf(AADConstants.OID_USER_PROPERTY_KEY to aadUserUID)

        if (allowCreatingNewUsersByLogin) {
            val createUser = myUserModel.createUserAccount(AADConstants.AAD_AUTH_SCHEME_NAME, userName)
            createUser.updateUserAccount(userName, displayName, email)
            createUser.setUserProperty(AADConstants.OID_USER_PROPERTY_KEY, aadUserUID)
        }

        return ServerPrincipal(AADConstants.AAD_AUTH_SCHEME_NAME, userName, null, allowCreatingNewUsersByLogin, userProperties)
    }

    private fun findExistingUserByEmail(email: String): SUser? {
        return myUserModel.allUsers.users.firstOrNull { email.equals(it.email, ignoreCase = true) }
    }

    private fun findExistingUserByUID(userUID: String): SUser? {
        val userSet = myUserModel.findUsersByPropertyValue(AADConstants.OID_USER_PROPERTY_KEY, userUID, true)
        return userSet.users.firstOrNull()
    }

    companion object {
        private val LOG = Logger.getLogger(ServerPrincipalFactory::class.java.name)
        private val DEFAULT_ALLOW_CREATING_NEW_USERS_BY_LOGIN = true

        private fun allowMatchUserByEmail(schemeProperties: Map<String, String>): Boolean =
                schemeProperties[AADConstants.ALLOW_MATCHING_USERS_BY_EMAIL]?.toBoolean() ?: false

        private fun allowUserDetailsSync(schemeProperties: Map<String, String>): Boolean =
                schemeProperties[AADConstants.ALLOW_USER_DETAILS_SYNC]?.toBoolean() ?: false
    }
}
