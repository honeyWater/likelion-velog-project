package org.example.velogproject.validator;

import lombok.RequiredArgsConstructor;
import org.example.velogproject.dto.UserRegisterDto;
import org.example.velogproject.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
@RequiredArgsConstructor
public class CheckUsernameValidator extends AbstractValidator<UserRegisterDto> {
    private final UserRepository userRepository;

    @Override
    protected void doValidate(UserRegisterDto dto, Errors errors) {
        if (userRepository.existsByUsername(dto.toEntity().getUsername())) {
            errors.rejectValue("username", "이름 중복 오류", "이미 사용중인 이름 입니다.");
        }
    }
}
