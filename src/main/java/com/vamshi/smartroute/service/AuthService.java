package com.vamshi.smartroute.service;

import com.vamshi.smartroute.dto.AuthRequestDto;
import com.vamshi.smartroute.dto.AuthResponseDto;
import com.vamshi.smartroute.entity.UserEntity;
import com.vamshi.smartroute.repository.UserRepository;
import com.vamshi.smartroute.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository,
                       PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public String registerUser(String username, String email, String rawPassword, String role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }

        UserEntity user = UserEntity.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(role == null ? "ROLE_USER" : role)
                .build();

        userRepository.save(user);
        return "User registered successfully";
    }

    public AuthResponseDto login(AuthRequestDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        UserEntity user = userRepository.findByUsername(loginDto.username())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new AuthResponseDto(token, user.getUsername(), user.getRole());
    }
}
