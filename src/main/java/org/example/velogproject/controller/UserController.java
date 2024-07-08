package org.example.velogproject.controller;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.velogproject.domain.Role;
import org.example.velogproject.domain.SocialLoginInfo;
import org.example.velogproject.domain.User;
import org.example.velogproject.dto.UserLoginDto;
import org.example.velogproject.dto.UserRegisterDto;
import org.example.velogproject.jwt.exception.JwtExceptionCode;
import org.example.velogproject.jwt.util.JwtTokenizer;
import org.example.velogproject.service.RefreshTokenService;
import org.example.velogproject.service.SocialLoginInfoService;
import org.example.velogproject.service.UserService;
import org.example.velogproject.validator.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
    private final JwtTokenizer jwtTokenizer;
    private final PasswordEncoder passwordEncoder;

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final SocialLoginInfoService socialLoginInfoService;

    private final CheckEmailValidator checkEmailValidator;
    private final CheckDomainValidator checkDomainValidator;
    private final CheckUsernameValidator checkUsernameValidator;
    private final CheckPasswordEqualValidator checkPasswordEqualValidator;
    private final CheckLoginAvailableValidator checkLoginAvailableValidator;

    /* 커스텀 유효성 검증을 위해 추가 */
    // @InitBinder를 이용하여 회원가입Dto에 대한 유효성 검사 추가
    @InitBinder("userRegisterDto")
    public void initUserRegisterDtoBinder(WebDataBinder binder) {
        binder.addValidators(checkUsernameValidator, checkEmailValidator, checkPasswordEqualValidator, checkDomainValidator);
    }

    // @InitBinder를 이용하여 로그인Dto에 대한 유효성 검사 추가
    @InitBinder("loginDto")
    public void initLoginDtoBinder(WebDataBinder binder) {
        binder.addValidators(checkLoginAvailableValidator);
    }

    // 회원가입 폼 화면 반환
    @GetMapping("/registerform")
    public String createUserRegisterForm(@RequestParam(value = "provider", required = false) String provider,
                                         @RequestParam(value = "socialId", required = false) String socialId,
                                         @RequestParam(value = "uuid", required = false) String uuid,
                                         @RequestParam(value = "username", required = false) String username,
                                         Model model) {

        UserRegisterDto userRegisterDto = new UserRegisterDto();
        // 여기까지 와서 provider가 존재한다는 것은 소셜로그인을 뜻한다.
        if (provider != null) {
            userRegisterDto.setProvider(provider);
            userRegisterDto.setSocialId(socialId);
            userRegisterDto.setUuid(uuid);
            userRegisterDto.setUsername(username);
        }
        model.addAttribute("userRegisterDto", userRegisterDto);

        return "user-register-form";
    }

    // 회원가입 수행 후 환영 페이지로 리다이렉트
    @PostMapping("/userreg")
    public String createUser(@Valid UserRegisterDto userRegisterDto,
                             Errors errors, Model model) {
        if (errors.hasErrors()) {
            // 회원가입 실패 시 입력 데이터 값을 유지
            model.addAttribute("userRegisterDto", userRegisterDto);

            // 유효성 통과 못한 필드와 메시지를 핸들링
            Map<String, String> validatorResult = userService.validateHandling(errors);
            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            // 회원가입 페이지로 다시 리턴
            return "user-register-form";
        }

        // 유효성 검증 후 처리
        Optional<SocialLoginInfo> socialLoginInfoOptional = socialLoginInfoService.findByProviderAndUuidAndSocialId(
            userRegisterDto.getProvider(), userRegisterDto.getUuid(), userRegisterDto.getSocialId()
        );

        if (socialLoginInfoOptional.isPresent()) {
            SocialLoginInfo socialLoginInfo = socialLoginInfoOptional.get();
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(socialLoginInfo.getCreatedAt(), now);

            if (duration.toMinutes() > 20) {
                return "redirect:/error";   // 회원가입이 20분 이상 경과한 경우
            }

            // 유효한 경우 User 정보를 저장
            userService.registUser(userRegisterDto, passwordEncoder);
            return "redirect:/welcome";
        } else {
            return "redirect:/error"; // 소셜 로그인 정보가 없는 경우 에러 페이지로 리다이렉트
        }
    }

    // 로그인 폼 화면 반환
    @GetMapping("/loginform")
    public String createLoginForm(Model model) {
        model.addAttribute("userLoginDto", new UserLoginDto());
        return "login-form";
    }

    // 로그인 수행
    @PostMapping("/login")
    public String doLogin(@Valid UserLoginDto userLoginDto, Errors errors, Model model,
                          HttpServletResponse response) {
        if (errors.hasErrors()) {
            userLoginDto.setPassword("");
            model.addAttribute("userLoginDto", userLoginDto);

            // 유효성 통과 못한 필드와 메시지를 핸들링
            Map<String, String> validatorResult = userService.validateHandling(errors);
            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return "login-form";
        }

        // 여기까지 왔다는 것은 가입한 사용자이며 비밀번호도 일치한다는 점
        // 역할 객체를 꺼내서 역할의 이름만 리스트로 얻어온다.
        User user = userService.getUserByEmail(userLoginDto.getEmail());
        List<String> roles = user.getRoles().stream().map(Role::getName).toList();

        // 토큰 발급 및 쿠키 설정
        jwtTokenizer.issueTokenAndSetCookies(response, user, roles);

        return "redirect:/";
    }

    /**
     * 1. 쿠키로부터 리프레시 토큰을 얻어온다.
     * 없을 경우 - 오류 페이지로 리다이렉트
     * 있을 경우
     * 1. 토큰으로부터 정보를 얻어온다.
     * 2. accessToken 생성
     * 3. 쿠키 생성 후 response 에 담고
     * 4. 메인페이지로 리다이렉트 한다.
     */
    @PostMapping("/refreshToken")
    public String getAccessTokenByRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;

        // HttpOnly 쿠키에서 refreshToken을 읽어오기
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        // refreshToken이 없으면 error 페이지로
        if (refreshToken == null) {
            return "redirect:/error";
        }

        // 토큰으로부터 정보 얻기
        Claims claims = jwtTokenizer.parseRefreshToken(refreshToken);
        Long userId = Long.valueOf((Integer) claims.get("userId"));
        User user = userService.getUserById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // accessToken 생성
        List<String> roles = ((List<?>) claims.get("roles")).stream()
            .filter(role -> role instanceof String)
            .map(role -> (String) role)
            .toList();
        String email = claims.getSubject();
        String accessToken = jwtTokenizer.createAccessToken(userId, email, user.getUsername(), roles);

        // 쿠키 생성 후 response 에 담기
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRATION_COUNT / 1000)); // 30분
        response.addCookie(accessTokenCookie);

        // 여기 오기 전의 요청을 기억해서 수행할 수 있나?
        return "redirect:/";
    }

    // 로그아웃
    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        // "accessToken" 및 "refreshToken" 쿠키 제거
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken") || cookie.getName().equals("refreshToken")) {
                    if (cookie.getName().equals("refreshToken")) {
                        refreshTokenService.deleteRefreshToken(cookie.getValue());
                    }
                    cookie.setValue(""); // null은 제거하는 것이 아닌 없는 것처럼 처리함
                    cookie.setPath("/");
                    cookie.setMaxAge(0); // 쿠키 만료 시간을 0으로 설정하여 제거
                    response.addCookie(cookie);
                }
            }
        }

        return "redirect:/";
    }

    // 환영 페이지
    @GetMapping("/welcome")
    public String showWelcomePage() {
        return "welcome";
    }

    // error 페이지
    @GetMapping("/error")
    public String error(HttpServletRequest request, Model model) {
        String exceptionCode = (String) request.getAttribute("exception");

        String message = switch (exceptionCode) {
            case "NOT_FOUND_TOKEN" -> JwtExceptionCode.NOT_FOUND_TOKEN.getMessage();
            case "INVALID_TOKEN" -> JwtExceptionCode.INVALID_TOKEN.getMessage();
            case "EXPIRED_TOKEN" -> JwtExceptionCode.EXPIRED_TOKEN.getMessage();
            case "UNSUPPORTED_TOKEN" -> JwtExceptionCode.UNSUPPORTED_TOKEN.getMessage();
            case "UNKNOWN_ERROR" -> JwtExceptionCode.UNKNOWN_ERROR.getMessage();
            default -> "error";
        };

        model.addAttribute("exception", message);
        return "error";
    }

    // 사용자 개인 블로그 메인 페이지
//    @GetMapping(value = {"/@{name}"})
}
