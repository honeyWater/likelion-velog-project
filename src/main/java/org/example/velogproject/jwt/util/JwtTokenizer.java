package org.example.velogproject.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.velogproject.domain.RefreshToken;
import org.example.velogproject.domain.User;
import org.example.velogproject.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class JwtTokenizer {
    private final RefreshTokenService refreshTokenService;
    private final byte[] accessSecret;
    private final byte[] refreshSecret;

    public static Long ACCESS_TOKEN_EXPIRATION_COUNT = 1 * 60 * 1000L; // 30분
    public static Long REFRESH_TOKEN_EXPIRATION_COUNT = 7 * 24 * 60 * 60 * 1000L; // 7일

    public JwtTokenizer(@Value("${jwt.secretKey}") String accessSecrete,
                        @Value("${jwt.refreshKey}") String refreshSecret,
                        RefreshTokenService refreshTokenService) {
        this.accessSecret = accessSecrete.getBytes(StandardCharsets.UTF_8);
        this.refreshSecret = refreshSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * AccessToken 생성
     */
    public String createAccessToken(Long id, String email, String username, List<String> roles) {
        return createToken(id, email, username, roles, ACCESS_TOKEN_EXPIRATION_COUNT, accessSecret);
    }

    /**
     * RefreshToken 생성
     */
    public String createRefreshToken(Long id, String email, String username, List<String> roles) {
        return createToken(id, email, username, roles, REFRESH_TOKEN_EXPIRATION_COUNT, refreshSecret);
    }

    private String createToken(Long id, String email, String username, List<String> roles,
                               Long expire, byte[] secretKey) {
        // 기본으로 가지고 있는 claim : subject
        Claims claims = Jwts.claims().setSubject(email);

        claims.put("roles", roles);
        claims.put("userId", id);
        claims.put("username", username);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date())
            .setExpiration(new Date(new Date().getTime() + expire))
            .signWith(getSigningKey(secretKey))
            .compact();
    }

    /**
     * 토큰에서 유저 아이디 얻기
     */
    public Long getUserIdFromToken(String token) {
        String[] tokenArr = token.split(" ");
        log.info(Arrays.toString(tokenArr));
        token = tokenArr[1];
        Claims claims = parseToken(token, accessSecret);
        return Long.valueOf((Integer) claims.get("userId"));
    }

    public Claims parseAccessToken(String accessToken) {
        return parseToken(accessToken, accessSecret);
    }

    public Claims parseRefreshToken(String refreshToken) {
        return parseToken(refreshToken, refreshSecret);
    }

    public Claims parseToken(String token, byte[] secretKey) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey(secretKey))
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    /**
     * @param secretKey - byte 형식
     * @return Key 형식의 시크릿 키
     */
    public static Key getSigningKey(byte[] secretKey) {
        return Keys.hmacShaKeyFor(secretKey);
    }

    /**
     * 토큰 발급 및 쿠키 설정
     */
    public void issueTokenAndSetCookies(HttpServletResponse response, User user, List<String> roles) {
        // 토큰 발급
        String accessToken
            = createAccessToken(user.getId(), user.getEmail(), user.getUsername(), roles);
        String refreshToken
            = createRefreshToken(user.getId(), user.getEmail(), user.getUsername(), roles);

        // 리프레쉬 토큰을 DB에 저장
        Optional<RefreshToken> refreshTokenOptional = refreshTokenService.findRefreshToken(user.getId());
        RefreshToken refreshTokenEntity;

        if (refreshTokenOptional.isPresent()) {
            refreshTokenEntity = refreshTokenOptional.get();
        } else {
            refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setUser(user);
        }
        refreshTokenEntity.setValue(refreshToken);
        refreshTokenService.saveRefreshToken(refreshTokenEntity);

        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true); // 접근 보안
        accessTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRATION_COUNT / 1000));
        // 쿠키의 유지시간 단위는 초, JWT의 시간 단위는 밀리세컨드이므로 1000으로 나눠줘야 함
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.REFRESH_TOKEN_EXPIRATION_COUNT));

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }
}
