package org.example.velogproject.validator;

import org.example.velogproject.dto.UserRegisterDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class CheckPasswordEqualValidator extends AbstractValidator<UserRegisterDto> {

    @Override
    protected void doValidate(UserRegisterDto dto, Errors errors) {
        if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
            errors.rejectValue("passwordConfirm", "비밀번호 일치 오류", "비밀번호가 일치하지 않습니다.");
        }
    }
}
