package org.example.velogproject.service;

import lombok.extern.slf4j.Slf4j;
import org.example.velogproject.dto.UserRegisterDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void getCountByEmail() {
        log.info("이메일 존재 ? " + userService.getCountByEmail("admin@naver.com"));
    }

    @Test
    void getCountByDomain() {
        log.info("닉네임 존재 ? " + userService.getCountByDomain("admin"));
    }

    @Test
    void registUser() {
        UserRegisterDto userRegisterDto = new UserRegisterDto();
//        userRegisterDto.setName("admin");
        userRegisterDto.setEmail("admin@naver.com");
        userRegisterDto.setPassword("1111");
        userRegisterDto.setDomain("admin");
//        userService.registUser(userRegisterDto);
    }

    @Test
    @DisplayName("PasswordEncoder 생성")
    void setPasswordEncoder(){
        log.info(passwordEncoder.encode("1111"));
    }
}