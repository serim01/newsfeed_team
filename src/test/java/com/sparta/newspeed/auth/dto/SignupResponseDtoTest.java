package com.sparta.newspeed.auth.dto;

import com.sparta.newspeed.testdata.UserMock;
import com.sparta.newspeed.user.entity.User;
import com.sparta.newspeed.user.entity.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SignupResponseDtoTest implements UserMock {

    @DisplayName("SignupResponseDto 생성 테스트")
    @Test
    void testSignupResponseDtoCreation() {
        User user = userMock;
        // when
        SignupResponseDto signupResponseDto = new SignupResponseDto(user);

        // then
        assertEquals(user.getUserSeq(), signupResponseDto.getUserSeq());
        assertEquals(USER_NAME, signupResponseDto.getUserName());
        assertEquals(USER_ID, signupResponseDto.getUserId());
        assertEquals(UserRoleEnum.USER, signupResponseDto.getRole());
    }
}