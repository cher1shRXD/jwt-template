package me.cher1shrxd.jwttemplate.domain.auth.service;

import lombok.RequiredArgsConstructor;

import me.cher1shrxd.jwttemplate.domain.auth.dto.request.LoginRequest;
import me.cher1shrxd.jwttemplate.domain.auth.dto.request.ReissueRequest;
import me.cher1shrxd.jwttemplate.domain.auth.dto.request.SignupRequest;
import me.cher1shrxd.jwttemplate.domain.auth.repository.RefreshTokenRepository;
import me.cher1shrxd.jwttemplate.domain.user.entity.UserEntity;
import me.cher1shrxd.jwttemplate.domain.user.enums.UserRole;
import me.cher1shrxd.jwttemplate.domain.user.repository.UserRepository;
import me.cher1shrxd.jwttemplate.global.exception.CustomErrorCode;
import me.cher1shrxd.jwttemplate.global.exception.CustomException;
import me.cher1shrxd.jwttemplate.global.security.jwt.dto.JwtResponse;
import me.cher1shrxd.jwttemplate.global.security.jwt.enums.JwtType;
import me.cher1shrxd.jwttemplate.global.security.jwt.provider.JwtProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public void signup(SignupRequest signupRequest) {
        String hashedPassword = bCryptPasswordEncoder.encode(signupRequest.password());

        if(userRepository.existsByUsername(signupRequest.username())) {
            throw new CustomException(CustomErrorCode.USERNAME_DUPLICATION);
        }

        if(userRepository.existsByEmail(signupRequest.email())) {
            throw new CustomException(CustomErrorCode.EMAIL_DUPLICATION);
        }

        UserEntity userEntity = UserEntity.builder()
                .username(signupRequest.username())
                .email(signupRequest.email())
                .password(hashedPassword)
                .nickname(signupRequest.nickname())
                .role(UserRole.USER)
                .build();

        userRepository.save(userEntity);
    }

    public JwtResponse login(LoginRequest loginRequest) {
        String email = loginRequest.email();
        String password = loginRequest.password();

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        if (!bCryptPasswordEncoder.matches(password, userEntity.getPassword()))
            throw new CustomException(CustomErrorCode.WRONG_PASSWORD);

        JwtResponse token = jwtProvider.generateToken(email);

        refreshTokenRepository.save(email, token.refreshToken());

        return token;
    }

    public JwtResponse reissue(ReissueRequest reissueRequest) {
        String refreshToken = reissueRequest.refreshToken();

        if (jwtProvider.getType(refreshToken) != JwtType.REFRESH)
            throw new CustomException(CustomErrorCode.INVALID_TOKEN_TYPE);

        String email = jwtProvider.getSubject(refreshToken);

        if (!refreshTokenRepository.existsByEmail(email))
            throw new CustomException(CustomErrorCode.INVALID_REFRESH_TOKEN);

        if (!refreshTokenRepository.findByEmail(email).equals(refreshToken))
            throw new CustomException(CustomErrorCode.INVALID_REFRESH_TOKEN);

        if (!userRepository.existsByUsername(email)) throw new CustomException(CustomErrorCode.USER_NOT_FOUND);

        return jwtProvider.generateToken(email);
    }
}
