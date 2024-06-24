package org.example.velogproject.validator;

import lombok.RequiredArgsConstructor;
import org.example.velogproject.dto.UserRegisterDto;
import org.example.velogproject.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
@RequiredArgsConstructor
public class CheckDomainValidator extends AbstractValidator<UserRegisterDto> {
    private final UserRepository userRepository;

    @Override
    protected void doValidate(UserRegisterDto dto, Errors errors) {
        if (userRepository.existsByDomain(dto.toEntity().getDomain())) {
            errors.rejectValue("domain", "도메인 중복 오류", "이미 사용중인 도메인 입니다.");
        }
    }
}
