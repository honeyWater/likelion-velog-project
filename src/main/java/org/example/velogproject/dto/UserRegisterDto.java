package org.example.velogproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.velogproject.domain.User;

@Data
@NoArgsConstructor
public class UserRegisterDto {
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String username;

    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;

    @NotBlank(message = "비밀번호를 확인해주세요.")
    private String passwordConfirm; // 비밀번호 확인에만 사용되는 필드

    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "도메인은 공백없이 영어와 숫자만 입력하세요.")
    @NotBlank(message = "도메인은 필수 입력 값입니다.")
    private String domain;

    private String velogName;
    private String info = "나만의 한줄소개를 작성해주세요.";
    private String profileImage = "D:/사용자/msi/Desktop/멋쟁이 사자처럼/velog 프로젝트/image/default.jpg";

    @Builder
    public UserRegisterDto(String username, String email, String password, String domain, String velogName, String info, String subInfo) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.domain = domain;
        this.velogName = velogName;
        this.info = info;
    }

    public User toEntity() {
        return User.builder()
                .username(username)
                .email(email)
                .password(password)
                .domain(domain)
                .velogName(velogName)
                .info(info)
                .profileImage(profileImage)
                .build();
    }
}
