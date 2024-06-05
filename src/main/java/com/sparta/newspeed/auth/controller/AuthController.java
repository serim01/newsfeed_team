package com.sparta.newspeed.auth.controller;

import com.sparta.newspeed.auth.dto.LoginRequestDto;
import com.sparta.newspeed.auth.dto.SignUpRequestDto;
import com.sparta.newspeed.auth.dto.SignupResponseDto;
import com.sparta.newspeed.auth.service.AuthService;
import com.sparta.newspeed.security.service.UserDetailsImpl;
import com.sparta.newspeed.security.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Tag(name = "인증 API",description = "인증 API")
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

    @Operation(summary = "회원가입",description = "회원가입")
    @PostMapping("/signup")
    public SignupResponseDto Signup(@RequestBody @Valid SignUpRequestDto requestDto){
        return authService.signup(requestDto);
    }

    @DeleteMapping("/logout")
    public String logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request, HttpServletResponse response) {
        request.removeAttribute(JwtUtil.AUTHORIZATION_HEADER);
        return "로그아웃 성공" + response.getStatus();
    }



}
