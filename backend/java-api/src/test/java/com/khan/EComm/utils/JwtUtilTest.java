package com.khan.EComm.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtUtil Unit Tests")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String TEST_SECRET = "5f4dcc3b5aa765d61d8327deb882cf995f4dcc3b5aa765d61d8327deb882cf99";
    private static final long ACCESS_EXPIRATION_MS = 900000L; // 15 mins
    private static final long REFRESH_EXPIRATION_MS = 604800000L; // 7 days

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKeyString", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", ACCESS_EXPIRATION_MS);
        ReflectionTestUtils.setField(jwtUtil, "refreshExpirationMs", REFRESH_EXPIRATION_MS);
        jwtUtil.init();
    }

    @Nested
    @DisplayName("Token Generation and Extraction Tests")
    class TokenGenerationAndExtractionTests {

        @Test
        @DisplayName("Should generate valid token and extract username correctly")
        void shouldGenerateTokenAndExtractUsername() {
            String email = "alice@example.com";

            String token = jwtUtil.generateToken(email);

            assertThat(token).isNotBlank();
            String extracted = jwtUtil.extractUsername(token);
            assertThat(extracted).isEqualTo(email);
        }

        @Test
        @DisplayName("Should generate valid UUID refresh token")
        void shouldGenerateValidRefreshToken() {
            String refreshToken = jwtUtil.generateRefreshToken();

            assertThat(refreshToken).isNotBlank();
            // Verify it is a valid UUID
            assertThat(UUID.fromString(refreshToken)).isNotNull();
        }
    }

    @Nested
    @DisplayName("Token Validation Tests")
    class TokenValidationTests {

        @Test
        @DisplayName("Should return true for valid non-expired token")
        void shouldReturnTrueForValidToken() {
            String token = jwtUtil.generateToken("user@example.com");

            boolean isValid = jwtUtil.validateToken(token);

            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("Should return false for token with invalid signature")
        void shouldReturnFalseForInvalidSignatureToken() {
            Key differentKey = Keys.hmacShaKeyFor("differentSecretKeyWithMinimumLength32Chars123456!".getBytes());
            String tokenSignedWithDifferentKey = Jwts.builder()
                    .setSubject("user@example.com")
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 60000))
                    .signWith(differentKey)
                    .compact();

            boolean isValid = jwtUtil.validateToken(tokenSignedWithDifferentKey);

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Should return false for malformed token string")
        void shouldReturnFalseForMalformedToken() {
            boolean isValid = jwtUtil.validateToken("not.a.valid.jwt.token");

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Should throw ExpiredJwtException when token is expired")
        void shouldThrowExpiredJwtExceptionForExpiredToken() {
            Key key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes());
            String expiredToken = Jwts.builder()
                    .setSubject("user@example.com")
                    .setIssuedAt(new Date(System.currentTimeMillis() - 20000))
                    .setExpiration(new Date(System.currentTimeMillis() - 10000)) // Expired in past
                    .signWith(key)
                    .compact();

            assertThatThrownBy(() -> jwtUtil.validateToken(expiredToken))
                    .isInstanceOf(ExpiredJwtException.class);
        }
    }

    @Nested
    @DisplayName("Cookie Extraction Tests")
    class CookieExtractionTests {

        @Test
        @DisplayName("Should extract accessToken from request cookies")
        void shouldExtractJwtFromCookies() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setCookies(new Cookie("accessToken", "mock-access-token-val"), new Cookie("other", "val"));

            String jwt = jwtUtil.getJwtFromCookies(request);

            assertThat(jwt).isEqualTo("mock-access-token-val");
        }

        @Test
        @DisplayName("Should return null when accessToken cookie is not present")
        void shouldReturnNullWhenJwtCookieMissing() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setCookies(new Cookie("other", "val"));

            String jwt = jwtUtil.getJwtFromCookies(request);

            assertThat(jwt).isNull();
        }

        @Test
        @DisplayName("Should return null when request has no cookies at all")
        void shouldReturnNullWhenCookiesNull() {
            MockHttpServletRequest request = new MockHttpServletRequest();

            String jwt = jwtUtil.getJwtFromCookies(request);

            assertThat(jwt).isNull();
        }

        @Test
        @DisplayName("Should extract refreshToken from request cookies")
        void shouldExtractRefreshTokenFromCookies() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setCookies(new Cookie("refreshToken", "mock-refresh-token-val"));

            String refresh = jwtUtil.getRefreshTokenFromCookies(request);

            assertThat(refresh).isEqualTo("mock-refresh-token-val");
        }
    }

    @Nested
    @DisplayName("Cookie Generation Tests")
    class CookieGenerationTests {

        @Test
        @DisplayName("Should generate valid accessToken ResponseCookie")
        void shouldGenerateJwtCookie() {
            ResponseCookie cookie = jwtUtil.generateJwtCookie("sample-jwt");

            assertThat(cookie.getName()).isEqualTo("accessToken");
            assertThat(cookie.getValue()).isEqualTo("sample-jwt");
            assertThat(cookie.getPath()).isEqualTo("/");
            assertThat(cookie.isHttpOnly()).isTrue();
            assertThat(cookie.getSameSite()).isEqualTo("Lax");
            assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(ACCESS_EXPIRATION_MS / 1000);
        }

        @Test
        @DisplayName("Should generate valid refreshToken ResponseCookie")
        void shouldGenerateRefreshTokenCookie() {
            ResponseCookie cookie = jwtUtil.generateRefreshJwtCookie("sample-refresh");

            assertThat(cookie.getName()).isEqualTo("refreshToken");
            assertThat(cookie.getValue()).isEqualTo("sample-refresh");
            assertThat(cookie.getPath()).isEqualTo("/");
            assertThat(cookie.isHttpOnly()).isTrue();
            assertThat(cookie.getSameSite()).isEqualTo("Lax");
            assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(REFRESH_EXPIRATION_MS / 1000);
        }

        @Test
        @DisplayName("Should generate clean accessToken cookie with maxAge 0")
        void shouldGenerateCleanJwtCookie() {
            ResponseCookie cookie = jwtUtil.getCleanJwtCookie();

            assertThat(cookie.getName()).isEqualTo("accessToken");
            assertThat(cookie.getValue()).isEmpty();
            assertThat(cookie.getMaxAge().getSeconds()).isZero();
            assertThat(cookie.isHttpOnly()).isTrue();
        }

        @Test
        @DisplayName("Should generate clean refreshToken cookie with maxAge 0")
        void shouldGenerateCleanRefreshCookie() {
            ResponseCookie cookie = jwtUtil.getCleanRefreshCookie();

            assertThat(cookie.getName()).isEqualTo("refreshToken");
            assertThat(cookie.getValue()).isEmpty();
            assertThat(cookie.getMaxAge().getSeconds()).isZero();
            assertThat(cookie.isHttpOnly()).isTrue();
        }
    }
}
