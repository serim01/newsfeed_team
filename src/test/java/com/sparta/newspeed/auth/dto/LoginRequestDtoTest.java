package com.sparta.newspeed.auth.dto;

import com.sparta.newspeed.testdata.UserMock;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestDtoTest implements UserMock {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @DisplayName("로그인 요청 DTO 생성")
    @Nested
    class Login {

        @DisplayName("로그인 요청 성공")
        @Test
        void LoginRequestDTO_success() {
            // given
            LoginRequestDto loginRequestDto = new LoginRequestDto();
            loginRequestDto.setUserId(USER_ID);
            loginRequestDto.setPassword(PASSWORD);

            // when
            Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(loginRequestDto);

            // then
            assertThat(violations).isEmpty();
        }

        @DisplayName("로그인 요청 실패 - userId가 비어있음")
        @Test
        void LoginRequestDTO_fail_userId_blank() {
            // given
            LoginRequestDto loginRequestDto = new LoginRequestDto();
            loginRequestDto.setUserId("");
            loginRequestDto.setPassword(PASSWORD);

            // when
            Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(loginRequestDto);

            // then
            assertThat(violations).extracting("message").contains("must not be blank");
        }

        @DisplayName("로그인 요청 실패 - password가 비어있음")
        @Test
        void LoginRequestDTO_fail_password_blank() {
            // given
            LoginRequestDto loginRequestDto = new LoginRequestDto();
            loginRequestDto.setUserId(USER_ID);
            loginRequestDto.setPassword("");

            // when
            Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(loginRequestDto);

            // then
            assertThat(violations).extracting("message").contains("must not be blank");
        }
    }
}