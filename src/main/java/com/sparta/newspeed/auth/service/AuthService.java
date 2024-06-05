package com.sparta.newspeed.auth.service;

import com.sparta.newspeed.auth.dto.LoginRequestDto;
import com.sparta.newspeed.security.util.JwtUtil;
import com.sparta.newspeed.user.entity.User;
import com.sparta.newspeed.user.entity.UserRoleEnum;
import com.sparta.newspeed.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    public String login(LoginRequestDto requestDto, HttpServletResponse response) {
        String userId = requestDto.getUserId();
        String password = requestDto.getPassword();

        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
        );

        if((!passwordEncoder.matches(password, user.getUserPassword()))) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return jwtUtil.createToken(userId, UserRoleEnum.USER);
    }
}
