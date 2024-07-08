package org.example.velogproject.jwt.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.velogproject.jwt.exception.JwtExceptionCode;
import org.example.velogproject.jwt.token.JwtAuthenticationToken;
import org.example.velogproject.jwt.util.JwtTokenizer;
import org.example.velogproject.security.CustomUserDetails;
import org.example.velogproject.service.RefreshTokenService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenizer jwtTokenizer;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = getToken(request); // accessToken 얻어냄
        if (StringUtils.hasText(token)) {
            try {
                getAuthentication(token);
            } catch (ExpiredJwtException e) {
                log.error("Expired Token : {}", token);
                throw e;
            } catch (UnsupportedJwtException e) {
                log.error("Unsupported Token: {}", token);
                throw e;
            } catch (MalformedJwtException e) {
                log.error("Invalid Token: {}", token);
                throw e;
            } catch (IllegalArgumentException e) {
                log.error("Token not found: {}", token);
                throw e;
            } catch (Exception e) {
                log.error("JWT Filter - Internal Error: {}", token);
                throw new BadCredentialsException("JWT filter internal exception", e);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private void getAuthentication(String token) {
        Claims claims = jwtTokenizer.parseAccessToken(token);
//        String email = claims.getSubject();
//        Long userId = claims.get("userId", Long.class);
//        String name = claims.get("name", String.class);
        String username = claims.get("username", String.class);
        List<GrantedAuthority> authorities = getGrantedAuthorities(claims);

        CustomUserDetails userDetails = new CustomUserDetails(
            username,
            "",
            authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())
        );

        Authentication authentication = new JwtAuthenticationToken(authorities, userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
        List<String> roles = ((List<?>) claims.get("roles")).stream()
            .filter(role -> role instanceof String)
            .map(role -> (String) role)
            .toList();
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(() -> role);
        }
        return authorities;
    }
}
