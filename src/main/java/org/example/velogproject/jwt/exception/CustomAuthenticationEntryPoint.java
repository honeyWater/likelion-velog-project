package org.example.velogproject.jwt.exception;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // 시큐리티가 인증되지 않은 사용자가 (인증해야만 사용할 수 있는)리소스에 접근할 때, 동작하게 하는 인터페이스
    // 즉, 사용자가 인증되지 않았을 때 동작
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String exception = (String) request.getAttribute("exception");

        // 어떤 요청인지를 구분해야한다. RestController? Controller?
        if (isRestRequest(request)) {
            handleRestResponse(request, response, exception);
        } else {
            handlePageResponse(request, response, exception);
        }
    }

    private boolean isRestRequest(HttpServletRequest request) {
        String requestedWithHeader = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWithHeader) || request.getRequestURI().startsWith("/api/");
    }

    private void handleRestResponse(HttpServletRequest request, HttpServletResponse response, String exception) throws IOException {
        log.error("Rest Request - Commence Get Exception : {}", exception);

        if (exception != null) {
            // 추가적인 API 요청에 대한 예외 처리 로직을 여기에 추가할 수 있다.
        }
    }

    // 페이지로 요청이 들어왔을 때, 인증되지 않은 사용자라면 무조건 /error 로 리디렉션 시킨다.
    private void handlePageResponse(HttpServletRequest request, HttpServletResponse response, String exception) throws IOException {
        log.error("Page Request - Commence Get Exception : {}", exception);

        if (exception != null) {
            // 추가적인 페이지 요청에 대한 예외 처리 로직을 여기에 추가할 수 있다.
        }

        response.sendRedirect("/error");
    }

    private void setResponse(HttpServletResponse response, JwtExceptionCode exceptionCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        HashMap<String, Object> errorInfo = new HashMap<>();
        errorInfo.put("message", exceptionCode.getMessage());
        errorInfo.put("code", exceptionCode.getCode());
        Gson gson = new Gson();
        String responseJson = gson.toJson(errorInfo);
        response.getWriter().print(responseJson);
    }
}
