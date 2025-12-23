package com.boottech.springshirojwt.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * Utility class for creating, parsing, and validating JSON Web Tokens (JWT).
 *
 * <p>
 * This class uses an HMAC-SHA signing key and provides helper methods for:
 * <ul>
 *     <li>Generating JWTs for authenticated users</li>
 *     <li>Validating token integrity and expiration</li>
 *     <li>Extracting claims such as username and user ID</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class is stateless and thread-safe.
 * </p>
 */
public final class JwtUtil {

    private JwtUtil() {}

    /**
     * Secret key used for signing JWTs.
     *
     * <p>
     * In production, this value should be externalized and securely stored
     * (e.g., environment variable, secrets manager).
     * </p>
     */
    private static final String SECRET =
            "a3f9c8d4e1b76f2a9d0c5e4b8f6a1c2d7e9f0a4b3c5d6e8f1a2b4c7d9e0";

    /**
     * Token expiration time in milliseconds (1 hour).
     */
    private static final long EXPIRATION_MS = 3600_000;

    /**
     * Signing key derived from the configured secret.
     */
    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /**
     * Generates a signed JWT for the given username.
     *
     * @param username the authenticated user's username
     * @return a signed JWT string
     */
    public static String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .claim("username", username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(KEY)
                .compact();
    }

    /**
     * Parses the given JWT and returns its claims.
     *
     * @param token the JWT string
     * @return the JWT claims payload
     * @throws JwtException if the token is invalid or expired
     */
    public static Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Validates the given JWT.
     *
     * <p>
     * This method verifies the token signature and expiration.
     * </p>
     *
     * @param token the JWT string
     * @return {@code true} if the token is valid; {@code false} otherwise
     */
    public static boolean validate(String token) {
        try {
            Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Extracts the user ID from the JWT subject.
     *
     * @param token the JWT string
     * @return the user ID as a {@link UUID}
     * @throws IllegalArgumentException if the subject is not a valid UUID
     */
    public static UUID getUserId(String token) {
        return UUID.fromString(parse(token).getSubject());
    }

    /**
     * Extracts the username claim from the JWT.
     *
     * @param token the JWT string
     * @return the username stored in the token
     */
    public static String getUsername(String token) {
        return parse(token).get("username", String.class);
    }
}
