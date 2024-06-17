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

class SignUpRequestDtoTest implements UserMock {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @DisplayName("회원가입 요청 DTO 생성")
    @Nested
    class Signup {
        @DisplayName("회원가입 성공")
        @Test
        void SignupRequestDTO_success() {
            //given
            SignUpRequestDto signUpRequestDto = new SignUpRequestDto(USER_ID,PASSWORD,USER_NAME,EMAIL);
            //when
            Set<ConstraintViolation<SignUpRequestDto>> violations = validator.validate(signUpRequestDto);

            // then
            assertThat(violations).isEmpty();
        }

        @DisplayName("회원가입 실패_비밀번호")
        @Test
        void SignupRequestDTO_fail_invalidPassword() {
            //given
            SignUpRequestDto signUpRequestDto = new SignUpRequestDto(USER_ID,"123456789a",USER_NAME,EMAIL);
            //when
            Set<ConstraintViolation<SignUpRequestDto>> violations = validator.validate(signUpRequestDto);

            // then
            assertThat(violations).extracting("message").contains("비밀번호는 대소문자 포함 영문 + 숫자 + 특수문자를 최소 1글자씩 포함해야 합니다.");
        }

        @DisplayName("회원가입 실패_아이디")
        @Test
        void SignupRequestDTO_fail_invalidUserId() {
            //given
            SignUpRequestDto signUpRequestDto = new SignUpRequestDto("invalid_user_id!",PASSWORD,USER_NAME,EMAIL);
            //when
            Set<ConstraintViolation<SignUpRequestDto>> violations = validator.validate(signUpRequestDto);

            // then
            assertThat(violations).extracting("message").contains("아이디는 대소문자 포함 영문 + 숫자만을 허용합니다.(10 ~ 20)");
        }

        @DisplayName("회원가입 실패_이메일")
        @Test
        void SignupRequestDTO_fail_invalidEmail() {
            //given
            SignUpRequestDto signUpRequestDto =  new SignUpRequestDto(USER_ID,PASSWORD,USER_NAME,"invalid-email");
            //when
            Set<ConstraintViolation<SignUpRequestDto>> violations = validator.validate(signUpRequestDto);

            // then
            assertThat(violations).extracting("message").contains("must be a well-formed email address");
        }

        @DisplayName("회원가입 실패_유저명")
        @Test
        void SignupRequestDTO_fail_blankUserName() {
            //given
            SignUpRequestDto signUpRequestDto = new SignUpRequestDto(USER_ID,PASSWORD,"",EMAIL);
            //when
            Set<ConstraintViolation<SignUpRequestDto>> violations = validator.validate(signUpRequestDto);

            // then
            assertThat(violations).extracting("message").contains("must not be blank");
        }
    }
}