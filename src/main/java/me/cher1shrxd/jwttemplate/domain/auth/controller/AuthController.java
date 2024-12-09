package me.cher1shrxd.jwttemplate.domain.auth.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.cher1shrxd.jwttemplate.domain.auth.dto.request.LoginRequest;
import me.cher1shrxd.jwttemplate.domain.auth.dto.request.ReissueRequest;
import me.cher1shrxd.jwttemplate.domain.auth.dto.request.SignupRequest;
import me.cher1shrxd.jwttemplate.domain.auth.service.AuthService;
import me.cher1shrxd.jwttemplate.global.security.jwt.dto.JwtResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "AUTH")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public void signup(@RequestBody SignupRequest signupRequest) {
            authService.signup(signupRequest);
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/reissue")
    public JwtResponse reissue(@RequestBody ReissueRequest reissueRequest) {
        return authService.reissue(reissueRequest);
    }
}
