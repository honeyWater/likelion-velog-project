package org.example.velogproject.service;

import lombok.RequiredArgsConstructor;
import org.example.velogproject.domain.Role;
import org.example.velogproject.domain.User;
import org.example.velogproject.dto.UserRegisterDto;
import org.example.velogproject.repository.RoleRepository;
import org.example.velogproject.repository.UserRepository;
import org.example.velogproject.util.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // 특정 이름의 사용자가 존재하는지 카운트
    @Transactional(readOnly = true)
    public boolean getCountByName(String name) {
        return userRepository.existsByName(name);
    }

    // 특정 이메일의 사용자가 존재하는지 카운트
    @Transactional(readOnly = true)
    public boolean getCountByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // 특정 도메인의 사용자가 존재하는지 카운트
    @Transactional(readOnly = true)
    public boolean getCountByDomain(String domain) {
        return userRepository.existsByDomain(domain);
    }

    // UserRegisterDto -> User Entity 로의 저장, 권한도 함께 저장
    @Transactional
    public void registUser(UserRegisterDto userRegisterDto) {
        // 초기 블로그 명을 도메인으로 블로그 명 생성
        if (userRegisterDto.getVelogName() == null) {
            userRegisterDto.setVelogName(userRegisterDto.getDomain() + ".log");
        }

        // 사용자 정보를 받아와서 User 객체로 변환
        User user = userRegisterDto.toEntity();

        // 역할 설정
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER");
        roles.add(userRole);
        user.setRoles(roles);

        // 사용자 저장
        userRepository.save(user);
    }

    // 회원가입 시 , 유효성 및 중복 검사
    @Transactional(readOnly = true)
    public Map<String, String> validateHandling(Errors errors) {
        Map<String, String> validatorResult = new HashMap<>();

        // 유효성 및 중복 검사에 실패한 필드 목록을 받음
        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }
        return validatorResult;
    }

    // 이메일로 User 찾기
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // id로 User 찾기
    @Transactional(readOnly = true)
    public String getUserIdById(Long id) {
        if (userRepository.existsById(id)) {
            return id.toString();
        } else {
            return null;
        }
    }

    // 사용자의 쿠키 확인
    public String checkUserContext(){
        return UserContext.getUser();
    }
}
