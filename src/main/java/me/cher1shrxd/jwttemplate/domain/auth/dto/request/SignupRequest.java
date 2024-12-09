package me.cher1shrxd.jwttemplate.domain.auth.dto.request;

public record SignupRequest(
        String username,
        String email,
        String nickname,
        String password
) {
}
