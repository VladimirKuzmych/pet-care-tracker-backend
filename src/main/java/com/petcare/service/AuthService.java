package com.petcare.service;

import com.petcare.dto.LoginRequest;
import com.petcare.dto.LoginResponse;
import com.petcare.dto.RegisterRequest;
import com.petcare.model.User;
import com.petcare.repository.UserRepository;
import com.petcare.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder,
                      JwtUtil jwtUtil,
                      AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            User user = userRepository
                .findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtUtil.generateToken(user.getEmail());

            return new LoginResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }

    public LoginResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already exists: " + registerRequest.getEmail());
        }

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());

        return new LoginResponse(
            token,
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName()
        );
    }
}
