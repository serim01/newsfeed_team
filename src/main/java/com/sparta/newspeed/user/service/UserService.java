package com.sparta.newspeed.user.service;

import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.user.dto.UserInfoUpdateDto;
import com.sparta.newspeed.user.dto.UserPwRequestDto;
import com.sparta.newspeed.user.dto.UserResponseDto;
import com.sparta.newspeed.user.entity.User;
import com.sparta.newspeed.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        // TODO 패스워드 비교 로직 작성
    }
}
