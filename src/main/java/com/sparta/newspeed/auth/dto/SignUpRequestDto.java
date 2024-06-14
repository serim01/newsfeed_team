package com.sparta.newspeed.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class SignUpRequestDto {

    @Pattern(regexp = "^[a-zA-Z0-9]{10,20}$",
            message = "아이디는 대소문자 포함 영문 + 숫자만을 허용합니다.(10 ~ 20)")
    private String userId;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[`~!@#$%^])[a-zA-z\\d`~!@#$%^]{10,}$",
            message = "비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자를 최소 1글자씩 포함해야 합니다.")
    private String password;

    @NotBlank
    private String userName;

    @Email
    private String email;

    private boolean admin = false;
    private String adminToken = "";
}
