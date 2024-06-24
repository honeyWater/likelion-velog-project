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
    @GetMapping("/users/check-name")
    @ResponseBody
    public ResponseEntity<Boolean> checkName(@RequestParam String name) {
        boolean result = true;

        if (userService.getCountByName(name)) {
            result = false; // 이름이 존재하면 사용할 수 없음
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // email 중복 확인
    @GetMapping("/users/check-email")
    @ResponseBody
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean result = true;

        if (userService.getCountByEmail(email)) {
            result = false; // 이메일이 존재하면 사용할 수 없음
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // domain 중복 확인
    @GetMapping("/users/check-domain")
    @ResponseBody
    public ResponseEntity<Boolean> checkDomain(@RequestParam String domain) {
        boolean result = true;

        if (userService.getCountByDomain(domain)) {
            result = false; // 도메인이 존재하면 사용할 수 없음
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
