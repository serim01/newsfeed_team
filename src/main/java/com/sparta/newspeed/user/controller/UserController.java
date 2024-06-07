package com.sparta.newspeed.user.controller;

import com.sparta.newspeed.security.service.UserDetailsImpl;
import com.sparta.newspeed.user.dto.UserInfoUpdateDto;
import com.sparta.newspeed.user.dto.UserPwRequestDto;
import com.sparta.newspeed.user.dto.UserResponseDto;
import com.sparta.newspeed.user.dto.UserStatusDto;
import com.sparta.newspeed.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API", description = "User API 입니다")
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserResponseDto getUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getUser(userDetails.getUser().getUserSeq());
    }

    @PutMapping
    public UserInfoUpdateDto updateUser(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @RequestBody UserInfoUpdateDto requestDto) {
        return userService.updateUser(userDetails.getUser().getUserSeq(), requestDto);
    }

    @PutMapping("/password")
    public ResponseEntity<String> updateUserPassword(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @RequestBody @Valid UserPwRequestDto requestDto) {
        userService.updateUserPassword(userDetails.getUser().getUserSeq(), requestDto);
        return ResponseEntity.ok("Password updated");
    }

    @Operation(summary = "회원탈퇴", description = "회원의 상태를 변경")
    @PostMapping("/withdraw")
    public ResponseEntity<String> updateWithdraw(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                           @RequestBody @Valid UserStatusDto requestDto){
        userService.updateWithdraw(userDetails.getUser().getUserSeq(), requestDto);
        return ResponseEntity.ok("Update user withdraw");
    }
}
