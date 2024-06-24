package org.example.velogproject.validator;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Slf4j
@NoArgsConstructor
public abstract class AbstractValidator<T> implements Validator {

    // clazz 안에 유효성 검사를 하기 위한 클래스 타입이 들어온다.
    // 원래는 return true가 아닌 해당 클래스가 유효성 검사가 가능한지 알아봐야 함
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @SuppressWarnings("unchecked") // 컴파일러에서 경고하지 않도록 하기 위해 사용
    @Override
    public void validate(Object target, Errors errors) {
        try {
            doValidate((T) target, errors);
        } catch (RuntimeException e) {
            log.error("중복 검증 에러");
            throw e;
        }
    }

    protected abstract void doValidate(final T dto, final Errors errors);
}
