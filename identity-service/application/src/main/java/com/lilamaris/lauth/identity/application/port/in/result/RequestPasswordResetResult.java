package com.lilamaris.lauth.identity.application.port.in.result;

import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;

import java.time.Instant;

public record RequestPasswordResetResult(
        Instant issuedAt,
        Instant expiresAt
) {
    public RequestPasswordResetResult {
        ObjectPrecondition.requireNonNull(issuedAt, "issuedAt");
        ObjectPrecondition.requireNonNull(expiresAt, "expiresAt");
    }

    public static RequestPasswordResetResult of(Instant issuedAt, Instant expiresAt) {
        return new RequestPasswordResetResult(issuedAt, expiresAt);
    }
}
