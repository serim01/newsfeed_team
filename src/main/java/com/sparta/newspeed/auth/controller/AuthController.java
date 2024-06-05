package com.sparta.newspeed.auth.controller;

import com.sparta.newspeed.auth.dto.LoginRequestDto;
import com.sparta.newspeed.auth.service.AuthService;
import com.sparta.newspeed.security.service.UserDetailsImpl;
import com.sparta.newspeed.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        String token = authService.login(requestDto, response);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
        return "로그인 성공" + response.getStatus();
    }
    @PostMapping("/signup")
    public String signup(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        authService.signup(requestDto);
        return "회원가입 성공" + response.getStatus();
    }
    @DeleteMapping("/logout")
    public String logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request, HttpServletResponse response) {
        request.removeAttribute(JwtUtil.AUTHORIZATION_HEADER);
        return "로그아웃 성공" + response.getStatus();
    }
}
