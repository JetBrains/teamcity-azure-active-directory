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