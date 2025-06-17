package com.example.QuizApp.controller;

import com.example.QuizApp.config.JwtUtil;
import com.example.QuizApp.dto.QuizResultResponse;
import com.example.QuizApp.dto.QuizSubjectResponse;
import com.example.QuizApp.dto.UserProfileResponse;
import com.example.QuizApp.model.Question;
import com.example.QuizApp.model.Quiz;
import com.example.QuizApp.model.User;
import com.example.QuizApp.repository.QuizRepository;
import com.example.QuizApp.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;

    private final QuizRepository quizRepository;

    @GetMapping("/profile")
    public UserProfileResponse getProfile(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserProfileResponse(
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toList())
        );
    }

    // Get quizzes by subject names
    @GetMapping("/quizSubjects")
    public List<QuizSubjectResponse> getQuizSubjectsOnly() {
        return quizRepository.findAll().stream()
                .map(quiz -> new QuizSubjectResponse(
                        quiz.getId(),
                        quiz.getSubject(),
                        quiz.getQuestions() != null ? quiz.getQuestions().size() : 0
                ))
                .collect(Collectors.toList());
    }

    //get ques for a particular quiz
    @GetMapping("/quiz/{quizId}")
    public List<Question> getQuestionsForQuiz(@PathVariable String quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"))
                .getQuestions()
                .stream()
                .map(q -> Question.builder()
                        .id(q.getId())
                        .questionText(q.getQuestionText())
                        .optionA(q.getOptionA())
                        .optionB(q.getOptionB())
                        .optionC(q.getOptionC())
                        .optionD(q.getOptionD())
                        .build())
                .toList();
    }



    // Attempt a quiz and calculate score
    @PostMapping("/solve/{quizId}")
    public QuizResultResponse attemptQuiz(@PathVariable String quizId, @RequestBody List<String> answers) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<Question> questions = quiz.getQuestions();
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getCorrectAnswer().equalsIgnoreCase(answers.get(i))) {
                score++;
            }
        }
        return new QuizResultResponse(questions.size(), score, score);
    }

    //old one
//    @PostMapping("/attempt/{quizId}")
//    public int attemptQuiz(@PathVariable String quizId, @RequestBody List<String> answers) {
//        Optional<Quiz> quizOpt = quizRepository.findById(quizId);
//        if (quizOpt.isPresent()) {
//            List<Question> questions = quizOpt.get().getQuestions();
//            int score = 0;
//            for (int i = 0; i < questions.size(); i++) {
//                if (questions.get(i).getCorrectAnswer().equalsIgnoreCase(answers.get(i))) {
//                    score++;
//                }
//            }
//            return score;
//        } else {
//            throw new RuntimeException("Quiz not found");
//        }
//    }

}
