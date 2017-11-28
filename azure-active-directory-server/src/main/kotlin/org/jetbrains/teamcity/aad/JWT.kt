package org.jetbrains.teamcity.aad

import com.google.gson.*
import org.apache.commons.codec.binary.Base64
import org.apache.log4j.Logger
import org.jose4j.jwt.consumer.InvalidJwtException

import java.io.UnsupportedEncodingException

/**
 * @author Evgeniy.Koshkin
 */
class JWT private constructor(private val myContent: JsonObject) {

    fun getClaim(claimName: String): String? {
        val jsonElement = myContent.get(claimName)
        return jsonElement?.asString
    }

    companion object {
        private val LOG = Logger.getLogger(JWT::class.java.name)

        private val JWT_PARTS_DELIMITER = Regex("\\.")
        private val UTF8 = "UTF8"

        /**
         * @param jwtString Java Web Token string
         * @return parsed Java Web Token
         */
        fun parse(jwtString: String): JWT? {
            LOG.debug(String.format("Verifying JWT: %s", jwtString))
            try {
                JWTVerifier.verify(jwtString)
            } catch (e: InvalidJwtException) {
                LOG.warn("Failed to verify JWT from JWS payload " + jwtString, e)
                return null
            }

            val jwtParts = jwtString.split(JWT_PARTS_DELIMITER).dropLastWhile({ it.isEmpty() }).toTypedArray()
            if (jwtParts.size != 3) {
                LOG.warn(String.format("JWT is malformed since consist of %d parts instead of required 3.", jwtParts.size))
                return null
            }
            val jwsPayload = addPadding(jwtParts[1])
            val jsonElement: JsonElement
            try {
                val jwsPayloadBytes = jwsPayload.toByteArray(charset(UTF8))
                jsonElement = JsonParser().parse(String(Base64.decodeBase64(jwsPayloadBytes)))
            } catch (e: JsonSyntaxException) {
                LOG.warn("Failed to parse JWT from JWS payload " + jwsPayload, e)
                return null
            } catch (e: JsonParseException) {
                LOG.warn("Failed to parse JWT from JWS payload " + jwsPayload, e)
                return null
            } catch (e: UnsupportedEncodingException) {
                LOG.warn("Failed to parse JWT from JWS payload " + jwsPayload, e)
                return null
            }

            return JWT(jsonElement.asJsonObject)
        }

        private fun addPadding(base64EncodedString: String): String {
            val numCharsToPad = base64EncodedString.length % 4
            if (numCharsToPad == 0) {
                return base64EncodedString
            }
            val buf = StringBuffer(base64EncodedString)
            for (i in 0 until numCharsToPad) {
                buf.append('=')
            }
            return buf.toString()
        }
    }
}
