package com.lilamaris.lauth.identity.application.port.out;

import java.net.URI;
import java.time.Instant;

public interface PasswordResetMailSender {
    void send(String email, URI passwordResetUri, Instant expiresAt);
}
