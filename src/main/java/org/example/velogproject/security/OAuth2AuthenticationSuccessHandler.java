package org.example.velogproject.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.velogproject.domain.Role;
import org.example.velogproject.domain.SocialLoginInfo;
import org.example.velogproject.domain.User;
import org.example.velogproject.jwt.util.JwtTokenizer;
import org.example.velogproject.service.RefreshTokenService;
import org.example.velogproject.service.SocialLoginInfoService;
import org.example.velogproject.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final SocialLoginInfoService socialLoginInfoService;
    private final UserService userService;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 요청 경로에서 provider 추출
        // redirect-uri: "{baseUrl}/login/oauth2/code/{registrantionId}"
        String requestUri = request.getRequestURI();
        String provider = extractProviderFromUri(requestUri);

        // provider가 없는 경로 요청이 왔다는 것은 문제가 발생한 것
        if (provider == null) {
            response.sendRedirect("/error");
            return;
        }

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        // attributes 에 소셜로그인한 정보가 들어가 있는데, 소셜로그인 방식에 따라서 key가 달라질 수 있다.
        // OAuth2 로그인 방식(provider)이 많아질 때마다 조건문이 들어가야 한다.
        String socialId = attributes.get("id").toString();
        String username = attributes.get("name").toString();
        String avatarUrl = attributes.get("avatar_url").toString();

        Optional<User> userOptional = userService.findByProviderAndSocialId(provider, String.valueOf(socialId));
        if (userOptional.isPresent()) { // 회원 정보가 있으면 로그인 처리
            User user = userOptional.get();
            List<String> roles = user.getRoles().stream().map(Role::getName).toList();

            // 토큰 발급 및 쿠키 설정
            jwtTokenizer.issueTokenAndSetCookies(response, user, roles);

            // 이후 "/"로 redirect 되도록 해야함
            response.sendRedirect("/");

        } else { // 소셜로 아직 회원가입이 안되었을 때,
            // 소셜 로그인 정보 저장
            SocialLoginInfo socialLoginInfo = socialLoginInfoService.saveSocialLoginInfo(provider, String.valueOf(socialId));
            response.sendRedirect("/registerform?provider=" + provider +
                "&socialId=" + socialId + "&username=" + username + "&uuid=" + socialLoginInfo.getUuid() + "&avatarUrl=" + avatarUrl);
        }
    }

    private String extractProviderFromUri(String uri) {
        if (uri == null || uri.isBlank()) {
            return null;
        }

        if (!uri.startsWith("/login/oauth2/code/")) {
            return null;
        }

        // ex: /login/oauth2/code/github 일 때 -> github
        String[] segments = uri.split("/");
        return segments[segments.length - 1];
    }
}
