package com.ping.authservice.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    public void sendPostDeletedEmail(String email, String reason) throws MessagingException {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Your Post Has Been Deleted");
        simpleMailMessage.setText("Dear User,\n\n" +
                "We are writing to inform you that your post has been deleted from Ping.\n\n" +
                "Reason for deletion: " + reason + "\n\n" +
                "If you have any questions or concerns, please contact our support team.\n\n" +
                "Thank you for understanding.\n\n" +
                "The Ping Team");

        // Enable STARTTLS
        JavaMailSenderImpl senderImpl = (JavaMailSenderImpl) javaMailSender;
        Properties props = senderImpl.getJavaMailProperties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");

        javaMailSender.send(simpleMailMessage);
    }

    public void sendPasswordResetEmail(String email, String resetLink) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(email);
        helper.setSubject("Password Reset Request");

        String htmlContent = "<p>Dear User,</p>"
                + "<p>We received a request to reset your password. Click the button below to reset it:</p>"
                + "<a href=\"" + resetLink + "\" style=\"display: inline-block; padding: 10px 20px; font-size: 16px; color: #fff; background-color: #2BA500; text-decoration: none; border-radius: 5px;\">Reset Password</a>"
                + "<p>If you did not request a password reset, please ignore this email.</p>"
                + "<p>Thank you,<br/>The Ping Team</p>";

        helper.setText(htmlContent, true);

        // Enable STARTTLS
        JavaMailSenderImpl senderImpl = (JavaMailSenderImpl) javaMailSender;
        Properties props = senderImpl.getJavaMailProperties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");

        javaMailSender.send(mimeMessage);
    }

    public void sendBlockUnblockEmail(String email, boolean isBlocked, String reason) throws MessagingException {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(isBlocked ? "Account Blocked" : "Account Unblocked");
        simpleMailMessage.setText("Dear User,\n\n" +
                "Your account has been " + (isBlocked ? "blocked" : "unblocked") + ".\n\n" +
                "Reason: " + reason + "\n\n" +
                "If you have any questions or concerns, please contact our support team.\n\n" +
                "Thank you for understanding.\n\n" +
                "The Ping Team");

        // Enable STARTTLS
        JavaMailSenderImpl senderImpl = (JavaMailSenderImpl) javaMailSender;
        Properties props = senderImpl.getJavaMailProperties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");

        javaMailSender.send(simpleMailMessage);
    }

    public void sendReportResponseEmail(String email, String response, String userName) throws MessagingException {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Response to Your Report");
        simpleMailMessage.setText("Dear User,\n\n" +
                "We have reviewed your report regarding the post created by " + userName + " and here is our response:\n\n" +
                response + "\n\n" +
                "If you have any further questions or concerns, please contact our support team.\n\n" +
                "Thank you for your feedback.\n\n" +
                "The Ping Team");

        // Enable STARTTLS
        JavaMailSenderImpl senderImpl = (JavaMailSenderImpl) javaMailSender;
        Properties props = senderImpl.getJavaMailProperties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");

        javaMailSender.send(simpleMailMessage);
    }
}
