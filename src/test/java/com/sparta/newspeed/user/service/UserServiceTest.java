package com.sparta.newspeed.user.service;

import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.testdata.UserMock;
import com.sparta.newspeed.user.dto.UserInfoUpdateDto;
import com.sparta.newspeed.user.dto.UserPwRequestDto;
import com.sparta.newspeed.user.dto.UserResponseDto;
import com.sparta.newspeed.user.dto.UserStatusDto;
import com.sparta.newspeed.user.entity.User;
import com.sparta.newspeed.user.entity.UserRoleEnum;
import com.sparta.newspeed.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 테스트 인스턴스의 생성 단위를 클래스로 변경합니다.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest implements UserMock{

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    Long userSeq;

    @BeforeAll
    void init() {
        User user = userRepository.save(userMock);
        userSeq = user.getUserSeq();
    }

    @BeforeEach
    void setUp() {
        User user = userService.findById(userSeq);
        String password = "password";
        user.updatePassword(passwordEncoder.encode(password));
        user.updateRole(UserRoleEnum.USER);
        userRepository.save(user);
    }

    @DisplayName("유저를 가져온다")
    @Transactional(readOnly = true)
    @Test
    @Order(1)
    void getUser() {
        UserResponseDto getUser = userService.getUser(userSeq);

        assertEquals(getUser.getId(), USER_ID);
        assertEquals(getUser.getEmail(), EMAIL);
    }

    @DisplayName("유저를 가져온다 (findById)")
    @Test
    @Transactional(readOnly = true)
    @Order(2)
    void findById() {
        User user = userService.findById(userSeq);

        assertEquals(user.getUserId(), USER_ID);
    }

    @DisplayName("유저 정보를 변경한다.")
    @Test
    @Transactional
    @Order(3)
    void updateUser() {
        UserInfoUpdateDto updateUser = new UserInfoUpdateDto(
                "updateName",
                "update_intro"
        );
        userService.updateUser(userSeq, updateUser,null);

        User user = userService.findById(userSeq);

        assertEquals(updateUser.getName(), user.getUserName());
    }

    @DisplayName("비밀번호 변경")
    @Nested
    @Order(4)
    class updateUserPassword {
        @DisplayName("유저 비밀번호를 변경한다_성공")
        @Test
        void updateUserPassword_success() {
            // given
            User user = userService.findById(userSeq);
            String password = "password";
            String newPassword = "updatePassword";

            // 유저가 랜덤으로 생성되었기 때문에 패스워드를 알 수 없으므로 업데이트 후 테스트
            user.updatePassword(passwordEncoder.encode(password));
            userRepository.save(user);

            UserPwRequestDto requestDto = new UserPwRequestDto(password, newPassword);

            // when
            userService.updateUserPassword(user.getUserSeq(), requestDto);

            // then
            User updateUser = userService.findById(user.getUserSeq());
            assertTrue(passwordEncoder.matches(newPassword, updateUser.getUserPassword()));
        }
        @DisplayName("유저 비밀번호를 변경한다_현재 비밀번호 불일치")
        @Test
        void updateUserPassword_INCORRECT_PASSWORD() {
            // given
            User user = userService.findById(userSeq);
            String password = "password";
            String newPassword = "updatePassword";

            user.updatePassword(passwordEncoder.encode(password));
            userRepository.save(user);

            UserPwRequestDto requestDto = new UserPwRequestDto("password_invalid", newPassword);

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> userService.updateUserPassword(user.getUserSeq(), requestDto));

            // then
            assertEquals(exception.getErrorCode(), ErrorCode.INCORRECT_PASSWORD);
        }
        @DisplayName("유저 비밀번호를 변경한다_변경 비밀번호 동일")
        @Test
        void updateUserPassword_DUPLICATE_PASSWORD() {
            // given
            User user = userService.findById(userSeq);
            String password = "password";
            String newPassword = "password";

            user.updatePassword(passwordEncoder.encode(password));
            userRepository.save(user);

            UserPwRequestDto requestDto = new UserPwRequestDto(password, newPassword);

            // when
            CustomException exception = assertThrows(CustomException.class,
                    () -> userService.updateUserPassword(user.getUserSeq(), requestDto));

            // then
            assertEquals(exception.getErrorCode(), ErrorCode.DUPLICATE_PASSWORD);
        }
    }
    @DisplayName("회원 탈퇴")
    @Nested
    @Order(5)
    class withdrawUser {
        @Test
        @DisplayName("회원 탈퇴_성공")
        @Order(3)
        void withdrawUser_success() {
            // given
            User user = userService.findById(userSeq);
            String password = "password";

            user.updatePassword(passwordEncoder.encode(password));
            userRepository.save(user);

            UserStatusDto requestDto = new UserStatusDto(user.getUserId(), password);

            //when
            userService.updateWithdraw(userSeq,requestDto);

            //then
            User updateUser = userService.findById(userSeq);
            assertEquals(UserRoleEnum.WITHDRAW, updateUser.getRole());
        }

        @Test
        @DisplayName("회원 탈퇴_ID 불일치")
        @Order(1)
        void withdrawUser_USER_NOT_FOUND() {
            // given
            User user = userService.findById(userSeq);
            String password = "password";

            user.updatePassword(passwordEncoder.encode(password));
            userRepository.save(user);

            UserStatusDto requestDto = new UserStatusDto("invalid-id", password);

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> userService.updateWithdraw(userSeq,requestDto));

            //then
            assertEquals(exception.getErrorCode(), ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("회원 탈퇴_비밀번호 불일치")
        @Order(2)
        void withdrawUser_INCORRECT_PASSWORD() {
            // given
            User user = userService.findById(userSeq);
            String password = "password";

            user.updatePassword(passwordEncoder.encode(password));
            userRepository.save(user);

            UserStatusDto requestDto = new UserStatusDto(user.getUserId(), "invalid-password");

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> userService.updateWithdraw(userSeq,requestDto));

            //then
            assertEquals(exception.getErrorCode(), ErrorCode.INCORRECT_PASSWORD);
        }

        @Test
        @DisplayName("회원 탈퇴_이미 탈퇴한 회원")
        @Order(4)
        void withdrawUser_USER_NOT_VALID() {
            // given
            User user = userService.findById(userSeq);
            String password = "password";

            user.updatePassword(passwordEncoder.encode(password));
            user.updateRole(UserRoleEnum.WITHDRAW);
            userRepository.save(user);

            UserStatusDto requestDto = new UserStatusDto(user.getUserId(), password);

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> userService.updateWithdraw(userSeq,requestDto));

            //then
            assertEquals(exception.getErrorCode(), ErrorCode.USER_NOT_VALID);
        }
    }
}