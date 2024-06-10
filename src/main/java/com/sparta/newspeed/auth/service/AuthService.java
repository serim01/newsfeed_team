package com.sparta.newspeed.auth.service;

import com.sparta.newspeed.auth.dto.LoginRequestDto;
import com.sparta.newspeed.auth.dto.SignUpRequestDto;
import com.sparta.newspeed.auth.dto.SignupResponseDto;
import com.sparta.newspeed.auth.dto.TokenResponseDto;
import com.sparta.newspeed.common.exception.CustomException;
import com.sparta.newspeed.common.exception.ErrorCode;
import com.sparta.newspeed.common.util.RedisUtil;
import com.sparta.newspeed.security.util.JwtUtil;
import com.sparta.newspeed.user.entity.User;
import com.sparta.newspeed.user.entity.UserRoleEnum;
import com.sparta.newspeed.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private int authNumber;


    public SignupResponseDto signup(SignUpRequestDto request) {
        String userId = request.getUserId();
        String userName = request.getUserName();
        String password = passwordEncoder.encode(request.getPassword());
        String email = request.getEmail();

        // 회원 아이디 중복 확인
        Optional<User> checkUsername = userRepository.findByUserId(userId);
        if (checkUsername.isPresent()) {
            throw new CustomException(ErrorCode.USER_NOT_UNIQUE);
        }
        Optional<User> checkEmail = userRepository.findByUserEmail(email);
        if (checkEmail.isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_NOT_UNIQUE);
        }else{
            joinEmail(email);
        }

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.UNCHECKED; // 아직 이메일 체크 안함.

        // 사용자 등록
        User user = User.builder()
                .userId(userId)
                .userName(userName)
                .userPassword(password)
                .userEmail(email)
                .role(role)
                .build();

        userRepository.save(user);

        return new SignupResponseDto(user);
    }

    @Transactional
    public boolean CheckAuthNum(String email, String authNum) {
        User user = userRepository.findByUserEmail(email).orElseThrow();
        if (redisUtil.getData(authNum) == null) {
            return false;
        } else if (redisUtil.getData(authNum).equals(email)) {
            user.updateRole(UserRoleEnum.USER);
            return true;
        } else {
            return false;
        }
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

    @Transactional
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
        TokenResponseDto token = jwtUtil.createToken(userId, UserRoleEnum.USER);
        user.setRefreshToken(token.getRefreshToken());
        userRepository.save(user);
        return jwtUtil.createToken(userId, UserRoleEnum.USER);
    }

    public void joinEmail(String email) {
        makeRandomNumber();
        String setFrom = "email@email.com"; //받는 이메일
        String toMail = email;
        String title = "회원 가입 인증 이메일 입니다.";
        String content =
                "뉴스피드 프로젝트 이메일 인증 메일입니다." +
                        "<br><br>" +
                        "인증 번호는 " + authNumber + "입니다." +
                        "<br>" +
                        "인증번호를 제대로 입력해주세요";
        mailSend(setFrom, toMail, title, content);
    }

    //이메일을 전송합니다.
    public void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {//이메일 서버에 연결할 수 없거나, 잘못된 이메일 주소를 사용하거나, 인증 오류가 발생하는 등 오류
            throw new CustomException(ErrorCode.EMAIL_FAIL);
        }

        redisUtil.setDataExpire(Integer.toString(authNumber), toMail, 60 * 3L); //3분
    }

    //이메일 인증번호 관련 : 임의의 6자리 양수를 반환합니다.
    public void makeRandomNumber() {
        Random r = new Random();
        String randomNumber = "";
        for (int i = 0; i < 6; i++) {
            randomNumber += Integer.toString(r.nextInt(10));
        }
        authNumber = Integer.parseInt(randomNumber);
    }
}
