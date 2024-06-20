package com.ping.authservice.util;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendOtpToEmail(String email, String otp) throws MessagingException {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Login in to Ping");
        simpleMailMessage.setText("Hello, This is your OTP for verifying your account in PING " + otp);

        // Enable STARTTLS
        JavaMailSenderImpl senderImpl = (JavaMailSenderImpl) javaMailSender;
        Properties props = senderImpl.getJavaMailProperties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");

        javaMailSender.send(simpleMailMessage);
    }
}
