package com.lilamaris.lauth.identity.application.model.event;

import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;

import java.net.URI;
import java.time.Instant;

public record PasswordResetRequested(
        String email,
        URI passwordResetUri,
        Instant expiresAt
) {
    public PasswordResetRequested {
        StringPrecondition.requireNonBlank(email, "email");
        ObjectPrecondition.requireNonNull(passwordResetUri, "passwordResetUri");
        ObjectPrecondition.requireNonNull(expiresAt, "expiresAt");
    }

    public static PasswordResetRequested of(String email, URI passwordResetUri, Instant expiresAt) {
        return new PasswordResetRequested(email, passwordResetUri, expiresAt);
    }
}
