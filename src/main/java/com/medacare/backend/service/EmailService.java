package com.medacare.backend.service;

import java.util.Random;
import com.medacare.backend.repository.UserRepository;

import jakarta.mail.internet.MimeMessage;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.mail.username}")
    private String medaCareEmail;

    public EmailService(JavaMailSender mailSender, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    public String sendVerificationEmail(User user, String subject, String bodyText, String headerText) {
        try {
            Random random = new Random();
            String verificationToken = String.valueOf(random.nextInt(122222, 999999));

            String body = "<div style='font-family: Arial, sans-serif;'>"
                    + "<div style='text-align: center;'><img src='cid:headerImage' alt='MedaCare' style='width: 100%; max-width: 600px;' /></div>"
                    + "<h1 style='color: #4a798b; text-align: center;'>" + headerText + "</h1>"
                    + "<div style='margin: 1em 10em'><h3 style='font-weight: 800; font-size: 17px'>Hello "
                    + user.getFirstName().substring(0, 1).toUpperCase() + user.getFirstName().substring(1) + ",</h3>"
                    + "<p style='font-size: 18px'>" + bodyText + "</p>"
                    + "<div><center><p style='text-align: center; font-size: 25px;color: #a55d68; background-color: #F5F5F5;"
                    + "padding: 0.3em 2em; width: fit-content; font-weight: 600;'>" + verificationToken
                    + "</p></center></div>"
                    + "</br></br>"
                    + "<p style='color: #407284; font-weight: 600; font-size: 14px'>Your Health, Anywhere</p>"
                    + "<p style='color:#848484; margin-top:20px; text-align: center;'>If you did not request this, please ignore this email.</p>"
                    + "</div></div>";

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
        helper.setFrom(medaCareEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);

        helper.addInline("headerImage", new ClassPathResource("static/images/MedaCareEmailHeader.jpg"));

        mailSender.send(message);
    }

    public String sendAutoAccountCreationEmail(User user, String generatedPassword, String introText) {
        try {
            String subject = "Account Has been Created for You!";
            String bodyText = introText + ". Your credentials are as follows"
                    + "<br><b>Username:</b> " + user.getEmail()
                    + "<br><b>Password:</b> " + generatedPassword;
            String body = "<div style='font-family: Arial, sans-serif; text-align: center;'>"
                    + "<img src='cid:headerImage' alt='MedaCare' style='width: 100%; max-width: 600px;' />"
                    + "<h2 style='color: #1d5775;'>Welcome to MedaCare!</h2>"
                    + "<p>" + bodyText + "</p>"
                    + "<h1 style='color: #b64c4c; font-size:11pt'>Please change your password as soon as possible!</h1>"
                    + "</br></br>"
                    + "<p style='color: #407284; font-weight: 600; font-size: 14px'>Your Health, Anywhere</p>"
                    + "<p style='color:#848484'>If you did not request this, please ignore this email.</p>"
                    + "</div>";

            sendEmail(user.getEmail(), subject, body);
            return generatedPassword;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error sending email";
        }

    }

    public String sendGeneralPurposeEmail(User user, String generatedPassword, String introText) {
        try {
            String subject = "Account Has been Created for You!";
            String bodyText = introText + ". Your credentials are as follows"
                    + "<br><b>Username:</b> " + user.getEmail()
                    + "<br><b>Password:</b> " + generatedPassword;
            String body = "<div style='font-family: Arial, sans-serif; text-align: center;'>"
                    + "<img src='cid:headerImage' alt='MedaCare' style='width: 100%; max-width: 600px;' />"
                    + "<h2 style='color: #1d5775;'>Welcome to MedaCare!</h2>"
                    + "<p>" + bodyText + "</p>"
                    + "<h1 style='color: #b64c4c; font-size:11pt'>Please change your password as soon as possible!</h1>"
                    + "</br></br>"
                    + "<p style='color: #407284; font-weight: 600; font-size: 14px'>Your Health, Anywhere</p>"
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
