package org.example.velogproject.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.velogproject.jwt.exception.JwtExceptionCode;
import org.example.velogproject.service.RefreshTokenService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final RefreshTokenService refreshTokenService;

    @ExceptionHandler(ExpiredJwtException.class)
    public void handleExpiredJwtException(HttpServletRequest request,
                                          HttpServletResponse response,
                                          ExpiredJwtException ex) throws IOException {
        if (hasRefreshToken(request)) {
            response.sendRedirect("/refreshToken");
        } else {
            request.setAttribute("exception", JwtExceptionCode.EXPIRED_TOKEN.getCode());
            log.error("Expired Token without RefreshToken: {}", ex.getMessage(), ex);
            response.sendRedirect("/error");
        }
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public void handleUnsupportedJwtException(HttpServletRequest request,
                                              HttpServletResponse response,
                                              UnsupportedJwtException ex) throws IOException {
        request.setAttribute("exception", JwtExceptionCode.UNSUPPORTED_TOKEN.getCode());
        log.error(ex.getMessage(), ex);
        response.sendRedirect("/error");
    }

    @ExceptionHandler(MalformedJwtException.class)
    public void handleMalformedJwtException(HttpServletRequest request,
                                            HttpServletResponse response,
                                            MalformedJwtException ex) throws IOException {
        request.setAttribute("exception", JwtExceptionCode.INVALID_TOKEN.getCode());
        log.error(ex.getMessage(), ex);
        response.sendRedirect("/error");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgumentException(HttpServletRequest request,
                                               HttpServletResponse response,
                                               IllegalArgumentException ex) throws IOException {
        request.setAttribute("exception", JwtExceptionCode.NOT_FOUND_TOKEN.getCode());
        log.error(ex.getMessage(), ex);
        response.sendRedirect("/error");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public void handleBadCredentialsException(HttpServletRequest request,
                                              HttpServletResponse response,
                                              BadCredentialsException ex) throws IOException {
        request.setAttribute("exception", JwtExceptionCode.UNKNOWN_ERROR.getCode());
        log.error(ex.getMessage(), ex);
        response.sendRedirect("/error");
    }

    private boolean hasRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    // DB에서 refreshToken 을 조회하여 일치하는지 확인
                    String refreshTokenValue = cookie.getValue();
                    return refreshTokenService.existsByValue(refreshTokenValue);
                }
            }
        }
        return false;
    }
}
