package com.sparta.newspeed.user.service;

import com.sparta.newspeed.auth.dto.SignUpRequestDto;
import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.security.service.UserDetailsImpl;
import com.sparta.newspeed.user.dto.UserInfoUpdateDto;
import com.sparta.newspeed.user.dto.UserPwRequestDto;
import com.sparta.newspeed.user.dto.UserResponseDto;
import com.sparta.newspeed.user.dto.UserStatusDto;
import com.sparta.newspeed.user.entity.User;
import com.sparta.newspeed.user.entity.UserRoleEnum;
import com.sparta.newspeed.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public UserResponseDto getUser(Long userSeq) {
        return new UserResponseDto(findById(userSeq));
    }

    public User findById(Long userSeq) {
        return userRepository.findById(userSeq).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
    }

    @Transactional
    public UserInfoUpdateDto updateUser(Long userSeq, UserInfoUpdateDto requestDto) {
        User user = findById(userSeq);
        user.updateUserInfo(requestDto);
        return new UserInfoUpdateDto(user);
    }

    @Transactional
    public void updateUserPassword(Long userSeq, UserPwRequestDto requestDto) {
        User user = findById(userSeq);
        if (!passwordEncoder.matches(requestDto.getOldPassword(), user.getUserPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }

        user.setUserPassword(passwordEncoder.encode(requestDto.getNewPassword()));
    }

    @Transactional
    public void Withdraw(Long userSeq, String password) {
        User user = findById(userSeq);
        if (!passwordEncoder.matches(password, user.getUserPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }
        // 회원 상태를 탈퇴로 변경
        user.UpdateRole(UserRoleEnum.WITHDRAW);
    }

}
