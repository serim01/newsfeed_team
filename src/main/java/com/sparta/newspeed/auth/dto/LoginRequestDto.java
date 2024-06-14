package com.sparta.newspeed.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LoginRequestDto {
    @NotBlank
    private String userId;
    @NotBlank
    private String password;
}