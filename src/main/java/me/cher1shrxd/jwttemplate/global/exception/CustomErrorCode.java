package me.cher1shrxd.jwttemplate.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomErrorCode {
    EXPIRED_JWT_TOKEN(401, "Expired JWT token"),
    INVALID_JWT_TOKEN(401, "Invalid JWT token"),
    UNSUPPORTED_JWT_TOKEN(401, "Unsupported JWT token"),
    MALFORMED_JWT_TOKEN(401, "Malformed JWT token"),
    INVALID_TOKEN_TYPE(401, "Invalid token type"),
    INVALID_REFRESH_TOKEN(401, "Invalid refresh token"),

    // AUTH
    USERNAME_DUPLICATION(409, "Username is already in use"),
    USER_NOT_FOUND(404, "User not found"),
    WRONG_PASSWORD(401, "Wrong password"),
    EMAIL_DUPLICATION(409, "Email is already in use"),

    // Global
    METHOD_ARGUMENT_NOT_SUPPORTED(405, "Method argument not supported"),
    NO_HANDLER_FOUND(404, "No handler found")
    ;

    private final int status;
    private final String message;
}
