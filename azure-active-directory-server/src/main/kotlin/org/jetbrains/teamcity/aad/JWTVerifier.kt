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

import org.jose4j.jwk.HttpsJwks
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver

object JWTVerifier {
    private val JWT_AAD_SIGNING_KEYS_ENDPOINT = "https://login.microsoftonline.com/fabrikamb2c.onmicrosoft.com/discovery/v2.0/keys"
    private val AADKeys = HttpsJwks(JWT_AAD_SIGNING_KEYS_ENDPOINT)

    /**
     * @param jwtString Java Web Token string
     */
    fun verify(jwtString: String): JwtClaims {
        val httpsJwksKeyResolver = HttpsJwksVerificationKeyResolver(AADKeys)
        val jwtConsumer = JwtConsumerBuilder()
                .setRequireExpirationTime() // the JWT must have an expiration time
                .setAllowedClockSkewInSeconds(3600) // allow some leeway in validating time based claims to account for clock skew
                .setRequireSubject() // the JWT must have a subject claim
                .setSkipDefaultAudienceValidation()
                .setVerificationKeyResolver(httpsJwksKeyResolver)
                .build()

        return jwtConsumer.processToClaims(jwtString)
    }
}