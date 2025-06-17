package com.example.QuizApp.repository;

import com.example.QuizApp.model.Role;
import com.example.QuizApp.model.User;
import com.example.QuizApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class UserRepositoryLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Insert admin only if not already present
        if (!userRepository.existsByUsername("harshal")) {
            User admin = User.builder()
                    .username("harshal")
                    .email("harshal.giri@mitaoe.ac.in")
                    .password(passwordEncoder.encode("Harshal@26"))
                    .roles(List.of(Role.ROLE_ADMIN))  // ✅ Fixed line
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Admin user created");
        }

        // Insert test user only if not already present
//        if (!userRepository.existsByUsername("user")) {
//            User user = User.builder()
//                    .username("user")
//                    .password(passwordEncoder.encode("user123"))
//                    .roles(List.of(Role.ROLE_USER))
//                    .build();
//            userRepository.save(user);
//            System.out.println("✅ User account created");
//        }
    }
}
