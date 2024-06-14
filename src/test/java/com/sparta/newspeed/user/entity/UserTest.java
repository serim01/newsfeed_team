package com.sparta.newspeed.user.entity;

import com.sparta.newspeed.testdata.UserMock;
import com.sparta.newspeed.user.dto.UserInfoUpdateDto;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserTest implements UserMock {

    @DisplayName("User 엔티티 생성 테스트")
    @Test
    @Order(1)
    void createUser() {
        // given
        User user = userMock;

        // then
        assertThat(user.getUserSeq()).isEqualTo(1L);
        assertThat(user.getUserName()).isEqualTo(USER_NAME);
        assertThat(user.getUserId()).isEqualTo(USER_ID);
        assertThat(user.getUserPassword()).isEqualTo(PASSWORD);
        assertThat(user.getUserEmail()).isEqualTo(EMAIL);
        assertThat(user.getRole()).isEqualTo(UserRoleEnum.USER);
    }

    @DisplayName("updateUserInfo 메서드 테스트")
    @Test
    void updateUserInfo() {
        // given
        UserInfoUpdateDto updateDto = new UserInfoUpdateDto("New Name", "New Intro");

        // when
        userMock.updateUserInfo(updateDto);

        // then
        assertThat(userMock.getUserName()).isEqualTo("New Name");
        assertThat(userMock.getUserIntro()).isEqualTo("New Intro");
    }

    @DisplayName("updateOAuth2Info 메서드 테스트")
    @Test
    void updateOAuth2Info() {
        // given
        String newUserName = "OAuth2 User";
        String newProfileImageUrl = "http://new.profile.url";

        // when
        userMock.updateOAuth2Info(newUserName, newProfileImageUrl);

        // then
        assertThat(userMock.getUserName()).isEqualTo(newUserName);
        assertThat(userMock.getProfileImageUrl()).isEqualTo(newProfileImageUrl);
    }

    @DisplayName("updatePassword 메서드 테스트")
    @Test
    void updatePassword() {
        // given
        String newPassword = "newPassword";

        // when
        userMock.updatePassword(newPassword);

        // then
        assertThat(userMock.getUserPassword()).isEqualTo(newPassword);
    }

    @DisplayName("updateRole 메서드 테스트")
    @Test
    void updateRole() {
        // given
        UserRoleEnum newRole = UserRoleEnum.ADMIN;

        // when
        userMock.updateRole(newRole);

        // then
        assertThat(userMock.getRole()).isEqualTo(newRole);
    }

    @DisplayName("setRefreshToken 메서드 테스트")
    @Test
    void setRefreshToken() {
        // given
        String refreshToken = "newRefreshToken";

        // when
        userMock.setRefreshToken(refreshToken);

        // then
        assertThat(userMock.getRefreshToken()).isEqualTo(refreshToken);
    }

    @DisplayName("setPhotoName 메서드 테스트")
    @Test
    void setPhotoName() {
        // given
        String photoName = "newPhotoName";

        // when
        userMock.setPhotoName(photoName);

        // then
        assertThat(userMock.getPhotoName()).isEqualTo(photoName);
    }
}
