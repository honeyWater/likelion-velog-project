package org.example.velogproject.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.velogproject.domain.User;
import org.example.velogproject.dto.LoginDto;
import org.example.velogproject.dto.UserRegisterDto;
import org.example.velogproject.service.UserService;
import org.example.velogproject.validator.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final CheckNameValidator checkNameValidator;
    private final CheckEmailValidator checkEmailValidator;
    private final CheckPasswordEqualValidator checkPasswordEqualValidator;
    private final CheckDomainValidator checkDomainValidator;
    private final CheckLoginAvailableValidator checkLoginAvailableValidator;

    /* 커스텀 유효성 검증을 위해 추가 */
    // @InitBinder를 이용하여 회원가입Dto에 대한 유효성 검사 추가
    @InitBinder("userRegisterDto")
    public void initUserRegisterDtoBinder(WebDataBinder binder) {
        binder.addValidators(checkNameValidator, checkEmailValidator, checkPasswordEqualValidator, checkDomainValidator);
    }

    // @InitBinder를 이용하여 로그인Dto에 대한 유효성 검사 추가
    @InitBinder("loginDto")
    public void initLoginDtoBinder(WebDataBinder binder) {
        binder.addValidators(checkLoginAvailableValidator);
    }

    // 로그인 폼 화면 반환
    @GetMapping("/loginform")
    public String createLoginForm(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "login-form";
    }

    // 로그인 수행
    @PostMapping("/login")
    public String doLogin(@Valid LoginDto loginDto, Errors errors, Model model,
                          HttpServletResponse response) {
        if (errors.hasErrors()) {
            loginDto.setPassword("");
            model.addAttribute("loginDto", loginDto);

            // 유효성 통과 못한 필드와 메시지를 핸들링
            Map<String, String> validatorResult = userService.validateHandling(errors);
            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            return "login-form";
        }

        User loginUser = userService.getUserByEmail(loginDto.getEmail());
        if (loginUser != null) {
            Cookie cookie = new Cookie("login", loginUser.getId().toString());
            cookie.setMaxAge(30 * 60); // 30분
            cookie.setPath("/"); // 쿠키가 사이트의 모든 경로에 대해 유효하다.
            cookie.setHttpOnly(true); // 자바스크립트에서 접근 불가

            response.addCookie(cookie);
        }

        return "redirect:/";
    }

    // 로그아웃
    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        // "login" 쿠키 제거
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("login")) {
                    cookie.setValue(""); // null은 제거하는 것이 아닌 없는 것처럼 처리함
                    cookie.setPath("/");
                    cookie.setMaxAge(0); // 쿠키 만료 시간을 0으로 설정하여 제거
                    response.addCookie(cookie);
                }
            }
        }

        return "redirect:/";
    }

    // 회원가입 폼 화면 반환
    @GetMapping("/registerform")
    public String createUserForm(Model model) {
        model.addAttribute("userRegisterDto", new UserRegisterDto());
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

        userService.registUser(userRegisterDto);
        return "redirect:/welcome";
    }

    // 환영 페이지
    @GetMapping("/welcome")
    public String showWelcomePage() {
        return "welcome";
    }

    // 사용자 개인 블로그 메인 페이지
//    @GetMapping(value = {"/@{name}"})
}
