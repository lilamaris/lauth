package com.lilamaris.lauth.identity.mail;

import com.lilamaris.lauth.identity.application.model.opaque.OpaqueToken;
import com.lilamaris.lauth.identity.application.port.out.PasswordResetMailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordResetTokenMailAdapter implements PasswordResetMailSender {
    @Override
    public void send(String email, OpaqueToken opaqueToken, Instant expiresAt) {
        log.warn("Mail sender is not currently supported. logging payload only.");
        log.info("Mail sender received payload. email={}, selector={}, expiresAt={}", email, opaqueToken.selector(), expiresAt);
    }
}
