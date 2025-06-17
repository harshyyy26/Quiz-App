package com.example.QuizApp.controller;

import com.example.QuizApp.config.JwtUtil;
import com.example.QuizApp.dto.JwtResponse;
import com.example.QuizApp.dto.LoginRequest;
import com.example.QuizApp.dto.SignupRequest;
import com.example.QuizApp.model.BlacklistedToken;
import com.example.QuizApp.model.Role;
import com.example.QuizApp.model.User;
import com.example.QuizApp.repository.TokenBlacklistRepository;
import com.example.QuizApp.repository.UserRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final com.example.QuizApp.service.UserDetailsServiceImpl userDetailsService;

    @Autowired
    private TokenBlacklistRepository tokenBlacklistRepository;

    //singup functionality
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singletonList(Role.ROLE_USER))
                .build();

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }


    //login functionality
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if (!userOptional.isPresent()) {
            // Try finding by email instead
            userOptional = userRepository.findByEmail(request.getUsername());
        }

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String token = jwtUtil.generateToken(user.getUsername()); // use username in token
                return ResponseEntity.ok(new JwtResponse(token));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }


    //logout functionality
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid or missing Authorization header");
        }

        String token = authHeader.replace("Bearer ", "").trim();

        if (token.isEmpty()) {
            return ResponseEntity.badRequest().body("Token is empty");
        }

        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token);
        tokenBlacklistRepository.save(blacklistedToken);

        return ResponseEntity.ok("Logged out successfully");
    }


    @Data
    static class AuthRequest {
        private String username;
        private String password;
    }
}
