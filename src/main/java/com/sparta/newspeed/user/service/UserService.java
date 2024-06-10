package com.sparta.newspeed.user.service;

import com.sparta.newspeed.auth.service.AuthService;
import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.common.util.RedisUtil;
import com.sparta.newspeed.user.dto.UserInfoUpdateDto;
import com.sparta.newspeed.user.dto.UserPwRequestDto;
import com.sparta.newspeed.user.dto.UserResponseDto;
import com.sparta.newspeed.user.dto.UserStatusDto;
import com.sparta.newspeed.user.entity.User;
import com.sparta.newspeed.user.entity.UserRoleEnum;
import com.sparta.newspeed.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final RedisUtil redisUtil;

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

        if (passwordEncoder.matches(requestDto.getNewPassword(), user.getUserPassword())) {
            throw new CustomException(ErrorCode.DUPLICATE_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(requestDto.getNewPassword()));
    }

    @Transactional
    public void updateWithdraw(Long userSeq, UserStatusDto requestDto) {
        User user = findById(userSeq);

        // ID가 일치하지 않을 경우
        if(!requestDto.getUserId().equals(user.getUserId())){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        // PW가 일치하지 않을 경우
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getUserPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }
        // 회원이 이미 탈퇴 상태일 경우
        if (user.getRole().equals(UserRoleEnum.WITHDRAW)){
            throw new CustomException(ErrorCode.USER_NOT_VALID);
        }

        // 회원 상태를 탈퇴로 변경
        user.updateRole(UserRoleEnum.WITHDRAW);
    }

    public void mailSend(String userEmail) {
        authService.joinEmail(userEmail);
    }

    @Transactional
    public Boolean CheckAuthNum(Long userSeq, String email, String authNum) {
        User user = findById(userSeq);
            if (redisUtil.getData(authNum) == null) {
                return false;
            } else if (redisUtil.getData(authNum).equals(email)) {
                user.updateRole(UserRoleEnum.USER);
                return true;
            } else {
                return false;
            }
        }

}
