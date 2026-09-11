package com.lilamaris.lauth.identity.application.internal.event;

import com.lilamaris.lauth.identity.application.port.out.PasswordResetMailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PasswordResetListener {
    private final PasswordResetMailSender mailSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PasswordResetRequested event) {
        mailSender.send(event.email(), event.opaqueToken(), event.expiresAt());
    }
}
