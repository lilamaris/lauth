package com.lilamaris.lauth.identity.application.port.out;

import com.lilamaris.lauth.identity.application.model.opaque.OpaqueToken;

import java.time.Instant;

public interface PasswordResetMailSender {
    void send(String email, OpaqueToken opaqueToken, Instant expiresAt);
}
