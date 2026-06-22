package cl.duoc.libroDigital.academicService.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String SECRET = "librodigital2026SecretKeyForJWTTokenGenerationAndValidation12345";

    private JwtUtil jwtUtil;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET);
        secretKey = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    @Test
    void extractUsernameEmailUserIdAndRoles() {
        String token = Jwts.builder()
                .setSubject("docente1")
                .claim("email", " docente@example.com ")
                .claim("userId", 42L)
                .claim("roles", "DOCENTE, ADMIN")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(secretKey)
                .compact();

        assertEquals("docente1", jwtUtil.extractUsername(token));
        assertEquals("docente@example.com", jwtUtil.extractEmail(token));
        assertEquals(42L, jwtUtil.extractUserId(token));
        assertEquals(List.of("DOCENTE", "ADMIN"), jwtUtil.extractRoles(token));
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void extractUserId_parsesStringClaim() {
        String token = Jwts.builder()
                .setSubject("user")
                .claim("userId", "15")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(secretKey)
                .compact();

        assertEquals(15L, jwtUtil.extractUserId(token));
    }

    @Test
    void extractRoles_returnsEmptyWhenMissing() {
        String token = Jwts.builder()
                .setSubject("user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(secretKey)
                .compact();

        assertEquals(Collections.emptyList(), jwtUtil.extractRoles(token));
        assertNull(jwtUtil.extractEmail(token));
        assertNull(jwtUtil.extractUserId(token));
    }

    @Test
    void validateToken_rejectsInvalidToken() {
        assertFalse(jwtUtil.validateToken("token.invalido"));
    }
}
