package org.example.velogproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.velogproject.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserRestController {
    private final UserService userService;

    // name 중복 확인
    @GetMapping("/users/check-username")
    @ResponseBody
    public ResponseEntity<Boolean> checkName(@RequestParam String username) {
        // 이름이 존재하면 사용할 수 없음
        boolean result = !userService.getCountByUsername(username);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // email 중복 확인
    @GetMapping("/users/check-email")
    @ResponseBody
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        // 이메일이 존재하면 사용할 수 없음
        boolean result = !userService.getCountByEmail(email);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // domain 중복 확인
    @GetMapping("/users/check-domain")
    @ResponseBody
    public ResponseEntity<Boolean> checkDomain(@RequestParam String domain) {
        // 도메인이 존재하면 사용할 수 없음
        boolean result = !userService.getCountByDomain(domain);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
