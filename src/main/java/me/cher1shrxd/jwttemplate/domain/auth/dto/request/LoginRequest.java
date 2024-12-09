package me.cher1shrxd.jwttemplate.domain.auth.dto;

public record LoginRequest(
        String email,
        String password
) {
}
