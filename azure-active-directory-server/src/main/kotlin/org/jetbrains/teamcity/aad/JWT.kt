

package org.jetbrains.teamcity.aad

import org.apache.log4j.Logger
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.InvalidJwtException

/**
 * @author Evgeniy.Koshkin
 */
class JWT private constructor(private val content: JwtClaims) {

    fun getClaim(claimName: String): String? = content.getClaimValue(claimName) as String?

    companion object {
        private val LOG = Logger.getLogger(JWT::class.java.name)

        /**
         * @param jwtString Java Web Token string
         * @return parsed Java Web Token
         */
        fun parse(jwtString: String): JWT? {
            LOG.debug(String.format("Verifying JWT: %s", jwtString))
            val claims = try {
                JWTVerifier.verify(jwtString)
            } catch (e: InvalidJwtException) {
                LOG.warn("Failed to parse JWT from JWS payload " + jwtString, e)
                return null
            }

            return JWT(claims)
        }
    }
}