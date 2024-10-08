package com.ping.postservice.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender javaMailSender;

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
}
