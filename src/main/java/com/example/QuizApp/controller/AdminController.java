package com.example.QuizApp.controller;

import com.example.QuizApp.model.Question;
import com.example.QuizApp.model.Quiz;
import com.example.QuizApp.model.User;
import com.example.QuizApp.repository.QuizRepository;
import com.example.QuizApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;
    // Add a new quiz subject
    @PostMapping("/addQuiz")
    public Quiz addQuiz(@RequestBody Quiz quiz) {
        return quizRepository.save(quiz);
    }

    // Add questions to existing quiz
    @PutMapping("/addQuestion/{quizId}")
    public Quiz addQuestion(@PathVariable String quizId, @RequestBody Question question) {
        Optional<Quiz> quizOpt = quizRepository.findById(quizId);
        if (quizOpt.isPresent()) {
            Quiz quiz = quizOpt.get();
            quiz.getQuestions().add(question);
            return quizRepository.save(quiz);
        } else {
            throw new RuntimeException("Quiz not found");
        }
    }

    // Delete quiz
    @DeleteMapping("/deleteQuiz/{quizId}")
    public String deleteQuiz(@PathVariable String quizId) {
        quizRepository.deleteById(quizId);
        return "Quiz deleted";
    }

    // Get all quizzes (for admin)
    @GetMapping("/quizzes")
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }


}
