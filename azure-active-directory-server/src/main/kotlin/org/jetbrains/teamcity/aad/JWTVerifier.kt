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

import jetbrains.buildServer.serverSide.TeamCityProperties
import org.jose4j.http.Get
import org.jose4j.jwk.HttpsJwks
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.atomic.AtomicReference

object JWTVerifier {
    private val TC_HTTPS_PROXY_HOST = "teamcity.https.proxyHost"
    private val TC_HTTPS_PROXY_PORT = "teamcity.https.proxyPort"
    private val HTTPS_PROXY_HOST = "https.proxyHost"
    private val HTTPS_PROXY_PORT = "https.proxyPort"
    private val CUSTOM_JWT_SIGNING_KEYS_ENDPOINT = "teamcity.aad.signingKeys.endpoint"

    private val JWT_AAD_SIGNING_KEYS_ENDPOINT = "https://login.microsoftonline.com/fabrikamb2c.onmicrosoft.com/discovery/v2.0/keys"
    private val httpJwksHolder = AtomicReference<HttpsJwksHolder?>()

    /**
     * @param jwtString Java Web Token string
     */
    fun verify(jwtString: String): JwtClaims {
        val httpsJwksKeyResolver = HttpsJwksVerificationKeyResolver(getHttpsJwks())
        val jwtConsumer = JwtConsumerBuilder()
                .setRequireExpirationTime() // the JWT must have an expiration time
                .setAllowedClockSkewInSeconds(3600) // allow some leeway in validating time based claims to account for clock skew
                .setRequireSubject() // the JWT must have a subject claim
                .setSkipDefaultAudienceValidation()
                .setVerificationKeyResolver(httpsJwksKeyResolver)
                .build()

        return jwtConsumer.processToClaims(jwtString)
    }

    private fun getHttpsJwks() : HttpsJwks {
        return httpJwksHolder.updateAndGet {
            val proxyHost = getFirstTeamCityProperty(TC_HTTPS_PROXY_HOST, HTTPS_PROXY_HOST) {
                TeamCityProperties.getPropertyOrNull(it).let { if (it.isNullOrBlank()) null else it  }
            }

            val proxyPort = getFirstTeamCityProperty(TC_HTTPS_PROXY_PORT, HTTPS_PROXY_PORT) {
                TeamCityProperties.getInteger(it).let{ if (it != 0) it else null }
            } ?: 443

            val proxyDescriptor = ProxyDescriptor(proxyHost, proxyPort)

            if (it?.proxyDescriptor?.equals(proxyDescriptor) ?: false) {
                it
            } else {
                HttpsJwksHolder(
                        proxyDescriptor,
                        HttpsJwks(getSigningKeysEndpoint()).also {
                            if (proxyDescriptor.proxyHost?.isNotBlank() ?: false) {
                                it.setSimpleHttpGet(Get().also {
                                    it.setHttpProxy(Proxy(Proxy.Type.HTTP, InetSocketAddress(proxyHost, proxyPort)))
                                })
                            }
                        }
                )
            }
        }!!.httpJwks
    }

    private fun getSigningKeysEndpoint(): String {
        return TeamCityProperties.getProperty(CUSTOM_JWT_SIGNING_KEYS_ENDPOINT, JWT_AAD_SIGNING_KEYS_ENDPOINT)
    }

    private fun <T> getFirstTeamCityProperty(vararg propertyNames: String, getter: (String) -> T?): T? {
        return propertyNames.map(getter).firstOrNull { it != null  }
    }

    private data class ProxyDescriptor(val proxyHost: String?, val proxyPort: Int?)

    private class HttpsJwksHolder(val proxyDescriptor: ProxyDescriptor, val httpJwks: HttpsJwks)
}