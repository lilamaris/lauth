package com.lilamaris.lauth.identity.mail;

import com.lilamaris.lauth.identity.application.model.opaque.OpaqueToken;
import com.lilamaris.lauth.identity.application.port.out.PasswordResetMailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordResetTokenMailAdapter implements PasswordResetMailSender {
    private final JavaMailSender mailSender;

    @Override
    public void send(String email, URI passwordResetUri, Instant expiresAt) {
        var message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("LAuth password reset confirmation");
        message.setText("Click this link for reset your account.\n" + passwordResetUri);
        message.setFrom("noreply@lilamaris.kr");

        mailSender.send(message);
    }
}
