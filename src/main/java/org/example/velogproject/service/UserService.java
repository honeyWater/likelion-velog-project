package org.example.velogproject.service;

import lombok.RequiredArgsConstructor;
import org.example.velogproject.domain.Role;
import org.example.velogproject.domain.User;
import org.example.velogproject.dto.UserRegisterDto;
import org.example.velogproject.repository.RoleRepository;
import org.example.velogproject.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // 특정 이름의 사용자가 존재하는지 카운트
    @Transactional(readOnly = true)
    public boolean getCountByUsername(String username) {
        return userRepository.existsByUsername(username);
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

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
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

    // provider, socialId로 User 찾기
    @Transactional(readOnly = true)
    public Optional<User> findByProviderAndSocialId(String provider, String socialId) {
        return userRepository.findByProviderAndSocialId(provider, socialId);
    }

    // UserRegisterDto -> User Entity 로의 저장, 권한도 함께 저장
    @Transactional
    public void registUser(UserRegisterDto userRegisterDto, PasswordEncoder passwordEncoder) {
        // 초기 블로그 명을 도메인으로 블로그 명 생성
        if (userRegisterDto.getVelogName() == null) {
            userRegisterDto.setVelogName(userRegisterDto.getDomain() + ".log");
        }

        // 여기까지 오면 passwordConfirm 도 확인한 것이므로 암호화해서 넣기
        userRegisterDto.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));

        // 사용자 정보를 받아와서 User 객체로 변환
        User user = userRegisterDto.toEntity();

        // 사용자 역할 추가
        Role userRole = roleRepository.findByName("USER");
        user.setRoles(Collections.singleton(userRole));

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
}
