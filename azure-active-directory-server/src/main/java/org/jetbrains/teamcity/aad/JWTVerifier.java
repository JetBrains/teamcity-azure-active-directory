package org.jetbrains.teamcity.aad;

import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;

public class JWTVerifier {
	private static final String JWT_AAD_SIGNING_KEYS_ENDPOINT = "https://login.microsoftonline.com/fabrikamb2c.onmicrosoft.com/discovery/v2.0/keys";
	private static final HttpsJwks AADKeys = new HttpsJwks(JWT_AAD_SIGNING_KEYS_ENDPOINT);
	
	/**
	* @param jwtString Java Web Token string
	*/
	public static void Verify(String jwtString)  throws InvalidJwtException {
	    HttpsJwksVerificationKeyResolver httpsJwksKeyResolver = new HttpsJwksVerificationKeyResolver(AADKeys);
	    JwtConsumer jwtConsumer = new JwtConsumerBuilder()
	            .setRequireExpirationTime() // the JWT must have an expiration time
	            .setAllowedClockSkewInSeconds(3600) // allow some leeway in validating time based claims to account for clock skew
	            .setRequireSubject() // the JWT must have a subject claim
	    		.setSkipDefaultAudienceValidation()
	            .setVerificationKeyResolver(httpsJwksKeyResolver)
	            .build();

	    jwtConsumer.processToClaims(jwtString);
	}
}