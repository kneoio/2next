package com.semantyca.core.jwt;

import io.smallrye.jwt.auth.principal.*;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@ApplicationScoped
@Alternative
@Priority(1)
public class JWTPrincipalFactory extends JWTCallerPrincipalFactory {

    @Override
    public JWTCallerPrincipal parse(String token, JWTAuthContextInfo authContextInfo) throws ParseException {
        if (token == null || token.isBlank()) {
            throw new ParseException("Token is missing or empty.");
        }

        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            throw new ParseException("Invalid JWT format. Expected a token with at least two parts (header.payload). Received: " + token);
        }

        try {
            String json = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            return new DefaultJWTCallerPrincipal(JwtClaims.parse(json));

        } catch (InvalidJwtException ex) {
            throw new ParseException("Failed to parse JWT claims: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new ParseException("Failed to decode Base64 payload: " + ex.getMessage());
        }
    }
}