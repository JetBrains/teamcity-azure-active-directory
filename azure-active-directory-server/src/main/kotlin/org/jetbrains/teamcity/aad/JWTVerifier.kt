

package org.jetbrains.teamcity.aad

import jetbrains.buildServer.serverSide.IOGuard
import jetbrains.buildServer.serverSide.TeamCityProperties
import jetbrains.buildServer.util.FuncThrow
import jetbrains.buildServer.util.StringUtil
import org.jose4j.http.Get
import org.jose4j.http.SimpleResponse
import org.jose4j.jwk.HttpsJwks
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.atomic.AtomicReference

object JWTVerifier {
    private val TC_HTTPS_PROXY_HOST = "teamcity.https.proxyHost"
    private val TC_HTTPS_PROXY_PORT = "teamcity.https.proxyPort"
    private val HTTPS_PROXY_HOST = "https.proxyHost"
    private val HTTPS_PROXY_PORT = "https.proxyPort"

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
                val httpsJwks = HttpsJwks(JWT_AAD_SIGNING_KEYS_ENDPOINT)
                val get = IOGuardWrapper(Get())
                httpsJwks.setSimpleHttpGet(get)

                if (proxyDescriptor.proxyHost?.isNotBlank() ?: false) {
                    get.setHttpProxy(Proxy(Proxy.Type.HTTP, InetSocketAddress(proxyHost, proxyPort)))
                }

                HttpsJwksHolder(proxyDescriptor, httpsJwks)
            }
        }!!.httpJwks
    }

    private fun <T> getFirstTeamCityProperty(vararg propertyNames: String, getter: (String) -> T?): T? {
        return propertyNames.map(getter).firstOrNull { it != null  }
    }

    private data class ProxyDescriptor(val proxyHost: String?, val proxyPort: Int?)

    private class HttpsJwksHolder(val proxyDescriptor: ProxyDescriptor, val httpJwks: HttpsJwks)

    private class IOGuardWrapper(private val delegate: Get): Get() {
        @Throws(IOException::class)
        override fun get(location: String?): SimpleResponse? {
            return IOGuard.allowNetworkCall(FuncThrow<SimpleResponse?, IOException> { delegate.get(location) })
        }
    }
}