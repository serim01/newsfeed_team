package com.sparta.newspeed.auth.service;

import com.sparta.newspeed.auth.dto.LoginRequestDto;
import com.sparta.newspeed.auth.dto.SignUpRequestDto;
import com.sparta.newspeed.auth.dto.SignupResponseDto;
import com.sparta.newspeed.auth.dto.TokenResponseDto;
import com.sparta.newspeed.auth.social.OAuthAttributes;
import com.sparta.newspeed.awss3.S3Service;
import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.security.util.JwtUtil;
import com.sparta.newspeed.user.entity.User;
import com.sparta.newspeed.user.entity.UserRoleEnum;
import com.sparta.newspeed.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final S3Service s3Service;

    public SignupResponseDto signup(SignUpRequestDto request, MultipartFile file) {
        String userId = request.getUserId();
        String userName = request.getUserName();
        String password = passwordEncoder.encode(request.getPassword());
        String email = request.getEmail();

        // 회원 아이디 중복 확인
        Optional<User> checkUsername = userRepository.findByUserId(userId);
        if (checkUsername.isPresent()) {
            throw new CustomException(ErrorCode.USER_NOT_UNIQUE);
        }

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        String fileName = null;
        if (file != null) {
            fileName = s3Service.uploadFile(file);
        }

        // 사용자 등록
        User user = User.builder()
                .userId(userId)
                .userName(userName)
                .userPassword(password)
                .userEmail(email)
                .role(role)
                .photoName(fileName)
                .build();

        userRepository.save(user);

        return new SignupResponseDto(user);
    }

    @Transactional
    public TokenResponseDto login(LoginRequestDto requestDto) {
        String userId = requestDto.getUserId();
        String password = requestDto.getPassword();

        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        if((!passwordEncoder.matches(password, user.getUserPassword()))) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }
        if(user.getRole().equals(UserRoleEnum.WITHDRAW)) {
            throw new CustomException(ErrorCode.USER_NOT_VALID);
        }
        TokenResponseDto token = jwtUtil.createToken(userId, UserRoleEnum.USER);
        user.setRefreshToken(token.getRefreshToken());
        userRepository.save(user);
        return token;
    }
    @Transactional
    public void logout(User user) {
        user.setRefreshToken(null);
        userRepository.save(user);
    }

    public TokenResponseDto reAuth(String refreshtoken) {
        String subToken = jwtUtil.substringToken(refreshtoken);
        User user = userRepository.findByRefreshToken(refreshtoken).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        if(!jwtUtil.validateRefreshToken(subToken)) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        }
        if(jwtUtil.substringToken(refreshtoken).equals(user.getRefreshToken())) {
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }
        String userId = jwtUtil.getUserInfoFromToken(subToken).getSubject();
        TokenResponseDto token = createToken(userId, UserRoleEnum.USER);
        updateRefreshToken(user, token.getRefreshToken());
        return jwtUtil.createToken(userId, UserRoleEnum.USER);
    }

    /**
     * 리프레시 토큰을 저장한다.
     *
     * @param user 유저 정보
     * @param refreshToken 저장할 리프레시 토큰
     */
    public void updateRefreshToken(User user, String refreshToken) {
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    /**
     * 존재하는 회원이라면 이름과 프로필 이미지 업데이트
     * 처음 가입하는 회원이라면 User 데이터 생성
     *
     * @param attributes 소셜 정보
     * @return 업데이트 & 생성된 User 데이터
     */
    public User saveOrUpdateOAuth2Info(OAuthAttributes attributes) {
        User user = userRepository.findByUserId(attributes.getEmail())
                .map(entity -> entity.updateOAuth2Info(attributes.getName(), attributes.getProfileImageUrl()))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }

    public TokenResponseDto createToken(String userId, UserRoleEnum role) {
        return jwtUtil.createToken(userId, role);
    }

}
