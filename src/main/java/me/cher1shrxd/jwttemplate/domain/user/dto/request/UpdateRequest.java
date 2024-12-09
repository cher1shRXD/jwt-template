package me.cher1shrxd.jwttemplate.domain.user.dto.request;

public record UpdateRequest(
        String username,
        String nickname,
        String password,
        String currentPassword
) {
}
