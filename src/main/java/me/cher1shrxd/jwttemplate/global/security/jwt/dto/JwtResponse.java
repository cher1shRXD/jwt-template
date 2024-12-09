package me.cher1shrxd.jwttemplate.global.security.jwt.dto;

public record JwtResponse(
        String accessToken,
        String refreshToken
) {
}
