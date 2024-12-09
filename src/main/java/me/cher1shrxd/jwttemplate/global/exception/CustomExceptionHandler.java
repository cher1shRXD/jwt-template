package me.cher1shrxd.jwttemplate.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomErrorResponse> handleCustomException(CustomException e) {
        return CustomErrorResponse.of(e.getCode()).toEntity();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return CustomErrorResponse.of(CustomErrorCode.METHOD_ARGUMENT_NOT_SUPPORTED).toEntity();
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException e) {
        return CustomErrorResponse.of(CustomErrorCode.NO_HANDLER_FOUND).toEntity();
    }
}