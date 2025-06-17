package com.example.QuizApp;

import com.example.QuizApp.model.Role;
import com.example.QuizApp.model.User;
import com.example.QuizApp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class QuizApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuizApplication.class, args);
	}

	@Bean
	CommandLineRunner run(UserRepository userRepo, PasswordEncoder encoder) {
		return args -> {
			if (userRepo.findByUsername("harshal").isEmpty()) {
				User admin = User.builder()
						.username("harshal")
						.email("harshal.giri@mitaoe.ac.in")
						.password(encoder.encode("Harshal@26"))
						.roles(List.of(Role.ROLE_ADMIN))
						.build();
				userRepo.save(admin);
			}
		};
	}
}
