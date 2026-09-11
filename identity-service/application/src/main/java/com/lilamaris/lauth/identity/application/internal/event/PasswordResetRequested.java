package com.lilamaris.lauth.identity.application.internal.event;

import com.lilamaris.lauth.identity.application.model.opaque.OpaqueToken;
import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;

import java.time.Instant;

public record PasswordResetRequested(
        String email,
        OpaqueToken opaqueToken,
        Instant expiresAt
) {
    public PasswordResetRequested {
        StringPrecondition.requireNonBlank(email, "email");
        ObjectPrecondition.requireNonNull(opaqueToken, "opaqueToken");
        ObjectPrecondition.requireNonNull(expiresAt, "expiresAt");
    }

    public static PasswordResetRequested of(String email, OpaqueToken opaqueToken, Instant expiresAt) {
        return new PasswordResetRequested(email, opaqueToken, expiresAt);
    }
}
