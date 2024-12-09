package me.cher1shrxd.jwttemplate.domain.user.service;

import lombok.RequiredArgsConstructor;
import me.cher1shrxd.jwttemplate.domain.user.dto.request.UpdateRequest;
import me.cher1shrxd.jwttemplate.domain.user.dto.response.UserResponse;
import me.cher1shrxd.jwttemplate.domain.user.entity.UserEntity;
import me.cher1shrxd.jwttemplate.domain.user.repository.UserRepository;
import me.cher1shrxd.jwttemplate.global.exception.CustomErrorCode;
import me.cher1shrxd.jwttemplate.global.exception.CustomException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserResponse getMe() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(email);

        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        return new UserResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail(), userEntity.getNickname(), userEntity.getRole());
    }

    public UserResponse updateMe(UpdateRequest updateRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        if(updateRequest.currentPassword() == null || !bCryptPasswordEncoder.matches(updateRequest.currentPassword(), userEntity.getPassword())) {
            throw new CustomException(CustomErrorCode.WRONG_PASSWORD);
        }

        if (updateRequest.username() != null) userEntity.setUsername(updateRequest.username());
        if (updateRequest.nickname() != null) userEntity.setNickname(updateRequest.nickname());
        if (updateRequest.password() != null) {
            String hashedPassword = bCryptPasswordEncoder.encode(updateRequest.password());
            userEntity.setPassword(hashedPassword);
        }

        userRepository.save(userEntity);
        System.out.println(userEntity);

        return new UserResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail(),userEntity.getNickname(), userEntity.getRole());
    }
}
