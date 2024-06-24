package org.example.velogproject.validator;

import lombok.RequiredArgsConstructor;
import org.example.velogproject.domain.User;
import org.example.velogproject.dto.LoginDto;
import org.example.velogproject.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
@RequiredArgsConstructor
public class CheckLoginAvailableValidator extends AbstractValidator<LoginDto> {
    private final UserRepository userRepository;

    @Override
    protected void doValidate(LoginDto dto, Errors errors) {
        if (!userRepository.existsByEmail(dto.getEmail())) {
            errors.rejectValue("email", "이메일 존재 오류", "계정이 존재하지 않습니다. 회원가입을 진행 해주세요.");
            return; // 유효성 검사 실패 시 바로 리턴
        }

        User user = userRepository.findByEmail(dto.getEmail());
        if (user == null || !dto.getPassword().equals(user.getPassword())) {
            errors.rejectValue("password", "비밀번호 일치 오류", "이메일과 비밀번호가 맞지 않습니다.");
        }
    }
}
