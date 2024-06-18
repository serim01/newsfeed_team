package com.sparta.newspeed.user.service;

import com.sparta.newspeed.security.service.UserDetailsServiceImpl;
import com.sparta.newspeed.testdata.UserMock;
import com.sparta.newspeed.user.entity.User;
import com.sparta.newspeed.user.entity.UserRoleEnum;
import com.sparta.newspeed.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest(properties = "spring.profiles.active=test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 테스트 인스턴스의 생성 단위를 클래스로 변경합니다.
class UserDetailsServiceImplTest implements UserMock {
    @Autowired
    UserRepository userRepository;

    @Mock
    UserRepository mockUserRepository;

    @BeforeAll
    void init(){
        userRepository.save(userMock);
    }

    @DisplayName("UserDetailsService 통합 테스트")
    @Test
    void test1() {
        List<User> findUser = userRepository.findAll();

        // given
        String userId = findUser.get(0).getUserId();

        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(userRepository);

        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

        // then
        assertNotNull(userDetails);
        assertEquals(userId, userDetails.getUsername());
    }

    @DisplayName("UserDetailsService 정상 단위 테스트")
    @Test
    void test2() {
        // given
        String userId = "test1";

        User user = User.builder()
                .userId(userId)
                .role(UserRoleEnum.USER)
                .build();

        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(mockUserRepository);

        // return 값 지정
        given(mockUserRepository.findByUserId(userId)).willReturn(Optional.of(user));

        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

        // then
        assertNotNull(userDetails);
        assertEquals(userId, userDetails.getUsername());
    }

    @DisplayName("UserDetailsService 탈퇴 회원 단위 테스트")
    @Test
    void test3() {
        // given
        String userId = "test1";

        User user = User.builder()
                .userId(userId)
                .role(UserRoleEnum.WITHDRAW)
                .build();

        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(mockUserRepository);

        // return 값 지정
        given(mockUserRepository.findByUserId(userId)).willReturn(Optional.of(user));

        // when - then
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(userId));
    }

    @DisplayName("UserDetailsService 존재하지 않는 회원 테스트")
    @Test
    void test4() {
        // given
        String userId = "test2";

        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(userRepository);

        // when - then
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(userId));
    }
}