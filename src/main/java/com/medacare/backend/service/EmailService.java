package com.medacare.backend.service;

import java.util.Random;
import com.medacare.backend.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.medacare.backend.model.User;

@Service
public class EmailService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    public String sendVerificationEmail(User user) {
        try{
            Random random = new Random();
        String verificationToken = String.valueOf(random.nextInt(122222, 999999));
        String subject = "MedaCare Email Verification Code";
        String body = "We received a request to signup to MedaCare. Please verify your email by entering the following code: "
                + verificationToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("throw0x1away@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
        return verificationToken;
        }
        catch(Exception ex){
            return "Error sending email";
        }

    }

}
