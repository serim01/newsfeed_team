package com.sparta.newspeed.user.entity;

import com.sparta.newspeed.common.Timestamped;
import com.sparta.newspeed.user.dto.UserInfoUpdateDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Getter
@Table(name = "users")
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_seq")
    private Long userSeq;

    @NotBlank
    @Column(name = "user_id")
    private String userId;

    @NotBlank
    @Column(name = "user_password")
    private String userPassword;

    @Column(name = "user_name")
    private String userName;

    @Email
    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "user_intro")
    private String userIntro;

    @Column(name = "user_status")
    private String userStatus;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "status_modified")
    private LocalDateTime statusModified;

    public void updateUserInfo(UserInfoUpdateDto requestDto) {
        this.userName = requestDto.getName();
        this.userEmail = requestDto.getEmail();
        this.userIntro = requestDto.getIntro();
    }

    public void setUserPassword(String encNewPassword) {
        this.userPassword = encNewPassword;
    }
}
