package org.example.velogproject.config;

import lombok.RequiredArgsConstructor;
import org.example.velogproject.jwt.exception.CustomAuthenticationEntryPoint;
import org.example.velogproject.jwt.filter.JwtAuthenticationFilter;
import org.example.velogproject.jwt.util.JwtTokenizer;
import org.example.velogproject.security.OAuth2AuthenticationSuccessHandler;
import org.example.velogproject.service.SocialUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final SocialUserService socialUserService;
    private final JwtTokenizer jwtTokenizer;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // rest api 설정
            .csrf(AbstractHttpConfigurer::disable) // csrf 비활성화 -> cookie를 사용하지 않으면 꺼도 된다. (cookie를 사용할 경우 httpOnly(XSS 방어), sameSite(CSRF 방어)로 방어해야 한다.)
            .cors(AbstractHttpConfigurer::disable) // cors 비활성화 -> 프론트와 연결 시 따로 설정 필요
            .httpBasic(AbstractHttpConfigurer::disable) // 기본 인증 로그인 비활성화
            .formLogin(AbstractHttpConfigurer::disable) // 기본 login form 비활성화
            .logout(AbstractHttpConfigurer::disable) // 기본 logout 비활성화
            .sessionManagement(c ->
                c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않음

            // request 인증, 인가 설정
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/registerform", "/userreg", "/error",
                    "/loginform", "/login", "/login/oauth2/code/github", "/logout", "/welcome",
                    "/", "/trending/{period}", "/recent",
                    "/@{domain}", "/@{domain}/", "/@{domain}/posts",
                    "/@{domain}/{slug}").permitAll()
                .requestMatchers("/api/users/**").permitAll() // 모든 /api/users 엔드포인트에 대해 접근 허용
                // 정적 리소스에 대한 접근 허용
                .requestMatchers("/css/**", "/js/**", "/profile_image/**", "thumbnail_image/**", "post_image/**").permitAll()
                .anyRequest().authenticated()
            )

            // oauth2 설정
            .oauth2Login(oauth2 -> oauth2 // OAuth2 로그인 기능에 대한 여러 설정의 진입점
                .loginPage("/loginform")
                .failureUrl("/error")
                // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정을 담당
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(this.oauth2UserService())
                )
                // 로그인 성공 시 핸들러
                .successHandler(oAuth2AuthenticationSuccessHandler)
            )

            // jwt 관련 설정
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenizer), UsernamePasswordAuthenticationFilter.class)

            // 인증 예외 핸들링
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(customAuthenticationEntryPoint)
            );

        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return oauth2UserRequest -> {
            OAuth2User oauth2User = delegate.loadUser(oauth2UserRequest);
            // 여기에 Github 유저 정보를 처리하는 로직을 추가할 수 있습니다.
            // 예: DB에 사용자 정보 저장, 권한 부여 등

            String token = oauth2UserRequest.getAccessToken().getTokenValue();

            // 사용자를 DB에 저장하거나 업데이트한다.
            String provider = oauth2UserRequest.getClientRegistration().getRegistrationId();
            String socialId = String.valueOf(oauth2User.getAttributes().get("id"));
            String username = (String) oauth2User.getAttributes().get("login");
            String avatarUrl = (String) oauth2User.getAttributes().get("avatar_url");

            socialUserService.saveOrUpdateUser(socialId, provider, username, avatarUrl);

            return oauth2User;
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
