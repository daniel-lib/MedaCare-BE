package com.medacare.backend.service;

import java.util.Random;
import com.medacare.backend.repository.UserRepository;

import jakarta.mail.internet.MimeMessage;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
        try {
            Random random = new Random();
            String verificationToken = String.valueOf(random.nextInt(122222, 999999));
            String subject = "MedaCare Email Verification Code";
            String bodyText = "We received a request to signup to MedaCare. Please <b>verify your email</b> by <b>entering the code</b> below";
            String body = "<div style='font-family: Arial, sans-serif; text-align: center;'>"
                    + "<img src='cid:headerImage' alt='MedaCare' style='width: 100%; max-width: 600px;' />"
                    + "<h2 style='color: #1d5775;'>Welcome to MedaCare!</h2>"
                    + "<p>" + bodyText + "</p>"
                    + "<h1 style='color: #b64c4c; font-size:13pt'>" + verificationToken + "</h1>"
                    + "</br>"
                    + "<p style='color:#848484'>If you did not request this, please ignore this email.</p>"
                    + "</div>";

            sendEmail(user.getEmail(), subject, body);

            return verificationToken;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error sending email";
        }

    }

    public void sendEmail(String to, String subject, String body) throws Exception {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("throw0x1away@gmail.com"); // change with variable from properties
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);

        helper.addInline("headerImage", new ClassPathResource("static/images/medacare_email_header.jpg"));

        mailSender.send(message);
    }

    public String sendInstitutionAccountCreationEmail(User user, String generatedPassword) {
        try {
            String subject = "Account Has been Created for You!";
            String bodyText = "We received a request to create account for your institution. Your credentials are as follows"
                    + "<br><b>Username:</b> " + user.getEmail()
                    + "<br><b>Password:</b> " + generatedPassword
                    + "<br><br>Please <b>verify your email</b> by <b>entering the code</b> below";
            String body = "<div style='font-family: Arial, sans-serif; text-align: center;'>"
                    + "<img src='cid:headerImage' alt='MedaCare' style='width: 100%; max-width: 600px;' />"
                    + "<h2 style='color: #1d5775;'>Welcome to MedaCare!</h2>"
                    + "<p>" + bodyText + "</p>"
                    + "<h1 style='color: #b64c4c; font-size:11pt'>Please change your password as soon as possible!</h1>"
                    + "</br>"
                    + "<p style='color:#848484'>If you did not request this, please ignore this email.</p>"
                    + "</div>";

            sendEmail(user.getEmail(), subject, body);
            return generatedPassword;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error sending email";
        }

    }

}
