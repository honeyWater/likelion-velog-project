package org.example.velogproject.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.example.velogproject.service.UserService;
import org.example.velogproject.util.UserContext;

import java.io.IOException;

@Setter
public class AuthenticationFilter implements Filter {
    private UserService userService;

    // 초기화 코드 (필요한 경우)
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        Cookie[] cookies = httpServletRequest.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies) {
                if("login".equals(cookie.getName())){
                    String userId = validateTokenAndGetUserId(cookie.getValue());
                    if(userId != null){
                        UserContext.setUser(userId);
                    }
                }
            }
        }

        try {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            UserContext.clear();
        }
    }

    // 정리 코드 (필요한 경우)
    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    // 토큰 검증 및 사용자 ID 추출 로직 (ex: JWT 검증)
    // 유효한 경우 사용자 ID를 반환, 그렇지 않으면 null 반환
    private String validateTokenAndGetUserId(String token){
        return userService.getUserIdById(Long.parseLong(token));
    }
}
