package com.example.QuizApp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Password Reset Request - Quiz App");

            // Temporary: include token directly in the email body for testing
            String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;

            String content = "<p>Hello,</p>"
                    + "<p>You requested a password reset. Use the following token in Postman:</p>"
                    + "<p><b>" + token + "</b></p>"
                    + "<p>Or click this link (if frontend is available):</p>"
                    + "<p><a href=\"" + resetLink + "\">Reset Password</a></p>"
                    + "<p>This link will expire in 15 minutes.</p>"
                    + "<br><p>Regards,<br><b>Quiz App Team</b></p>";

            helper.setText(content, true);
            helper.setFrom("harshal.giri@mitaoe.ac.in", "Quiz App");

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }



//    public void sendPasswordResetEmail(String to, String token) {
//        String resetLink = "https://your-frontend-domain.com/password-reset?token=" + token;
//        String subject = "Quiz App - Password Reset Request";
//        String content = "<p>Hello,</p>"
//                + "<p>You requested to reset your password. Click the link below to proceed:</p>"
//                + "<p><a href=\"" + resetLink + "\">Reset Password</a></p>"
//                + "<p>This link will expire in 15 minutes.</p>"
//                + "<br><p>Regards,<br><b>Quiz App Team</b></p>";
//
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(content, true);
//
//            // ✅ Set display name for sender
//            helper.setFrom("harshalgiri01@gmail.com", "Quiz App");
//
//            mailSender.send(message);
//        } catch (MessagingException | UnsupportedEncodingException e) {
//            throw new RuntimeException("Failed to send email", e);
//        }
//
//    }
}
