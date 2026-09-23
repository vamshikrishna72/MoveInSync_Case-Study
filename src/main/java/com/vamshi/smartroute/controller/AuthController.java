package com.vamshi.smartroute.controller;

import com.vamshi.smartroute.dto.AuthRequestDto;
import com.vamshi.smartroute.dto.AuthResponseDto;
import com.vamshi.smartroute.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody AuthRequestDto request) {
        String message = authService.registerUser(request.username(), request.username() + "@campus.com", request.password(), "ROLE_USER");
        return ResponseEntity.ok(message);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
        AuthResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
