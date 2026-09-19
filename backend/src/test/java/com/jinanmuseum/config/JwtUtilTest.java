package com.jinanmuseum.config;

import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private static final String SECRET = "jinan-museum-demo-secret-0123456789abcdef";

    @Test
    void roundTripReturnsUsernameAndValidates() {
        JwtUtil jwtUtil = new JwtUtil(SECRET, 12);
        String token = jwtUtil.generateToken("admin");
        assertEquals("admin", jwtUtil.getUsernameFromToken(token));
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void tamperedTokenFailsValidation() {
        JwtUtil jwtUtil = new JwtUtil(SECRET, 12);
        String token = jwtUtil.generateToken("admin");
        assertFalse(jwtUtil.validateToken(token + "x"));
    }

    @Test
    void secretShorterThan32BytesIsRejected() {
        assertThrows(WeakKeyException.class, () -> new JwtUtil("too-short", 12));
    }
}
