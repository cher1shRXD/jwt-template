package me.cher1shrxd.jwttemplate.domain.auth.dto.request;

public record LoginRequest(
        String email,
        String password
) {
}
