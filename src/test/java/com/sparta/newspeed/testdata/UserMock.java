package com.sparta.newspeed.testdata;

import com.sparta.newspeed.user.entity.User;
import com.sparta.newspeed.user.entity.UserRoleEnum;

public interface UserMock {
    String USER_NAME = "test";
    String USER_ID = "test123456789";
    String PASSWORD = "123456789a!";
    String EMAIL = "test@gmail.com";

    User userMock = User.builder()
            .userName(USER_NAME)
            .userId(USER_ID)
            .userPassword(PASSWORD)
            .userEmail(EMAIL).role(UserRoleEnum.USER).build();
    User another = User.builder()
            .userName(USER_NAME+"another")
            .userId(USER_ID)
            .userPassword(PASSWORD)
            .userEmail(EMAIL).role(UserRoleEnum.USER).build();
}
