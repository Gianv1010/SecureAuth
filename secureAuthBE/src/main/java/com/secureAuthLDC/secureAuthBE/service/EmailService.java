package com.secureAuthLDC.secureAuthBE.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    
    //va a prendere il valore che sta in application.yaml (app.mail.from) e se assente utilizza quello che sta dopo i :
    @Value("${app.mail.from:no-reply@secureauth.local}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(toEmail);
        message.setSubject("Reset password - SecureAuth");
        message.setText(
            "Hai richiesto il reset della password.\n\n" +
            "Clicca qui per impostare una nuova password:\n" +
            resetLink + "\n\n" +
            "Se non sei stato tu, ignora questa email.\n"
        );

        mailSender.send(message);
    }
}
