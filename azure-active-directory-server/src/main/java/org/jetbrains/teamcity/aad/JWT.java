package org.jetbrains.teamcity.aad;

import com.google.gson.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jose4j.jwt.consumer.InvalidJwtException;

import java.io.UnsupportedEncodingException;

/**
 * @author Evgeniy.Koshkin
 */
public class JWT {

  private static final Logger LOG = Logger.getLogger(JWT.class);

  private static final String JWT_PARTS_DELIMITER = "\\.";
  private static final String UTF8 = "UTF8";

  private final JsonObject myContent;

  private JWT(@NotNull JsonObject content) {
    myContent = content;
  }

  /**
   * @param jwtString Java Web Token string
   * @return parsed Java Web Token
   */
  @Nullable
  public static JWT parse(@NotNull String jwtString){
	LOG.debug (String.format("Verifying JWT: %s", jwtString));
	try {
		JWTVerifier.Verify(jwtString);
	} catch (InvalidJwtException e) {
		LOG.warn("Failed to verify JWT from JWS payload " + jwtString, e);
	    return null;
	}
    final String[] jwtParts = jwtString.split(JWT_PARTS_DELIMITER);
    if(jwtParts.length != 3){
      LOG.warn(String.format("JWT is malformed since consist of %d parts instead of required 3.", jwtParts.length));
      return null;
    }
    final String jwsPayload = addPadding(jwtParts[1]);
    final JsonElement jsonElement;
    try {
      final byte[] jwsPayloadBytes = jwsPayload.getBytes(UTF8);
      jsonElement = new JsonParser().parse(new String(Base64.decodeBase64(jwsPayloadBytes)));
    } catch (JsonSyntaxException e) {
      LOG.warn("Failed to parse JWT from JWS payload " + jwsPayload, e);
      return null;
    } catch (JsonParseException e){
      LOG.warn("Failed to parse JWT from JWS payload " + jwsPayload, e);
      return null;
    } catch (UnsupportedEncodingException e) {
      LOG.warn("Failed to parse JWT from JWS payload " + jwsPayload, e);
      return null;
    }
    return new JWT(jsonElement.getAsJsonObject());
  }

  private static String addPadding(String base64EncodedString) {
    int numCharsToPad = base64EncodedString.length() % 4;
    if (numCharsToPad  == 0) {
      return base64EncodedString;
    }
      StringBuffer buf = new StringBuffer(base64EncodedString);
      for(int i = 0; i < numCharsToPad; i++) {
        buf.append('=');
      }
    return buf.toString();
  }

  @Nullable
  public String getClaim(@NotNull String claimName){
    final JsonElement jsonElement = myContent.get(claimName);
    return jsonElement == null ? null : jsonElement.getAsString();
  }
}
