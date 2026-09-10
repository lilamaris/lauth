package com.lilamaris.lauth.identity.application.model.session;

import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

public record SessionContext(
        UUID sessionId,
        Instant createdAt,
        Instant expiresAt,
        @Nullable Instant revokedAt
) {
    public SessionContext {
        ObjectPrecondition.requireNonNull(sessionId, "sessionId");
        ObjectPrecondition.requireNonNull(createdAt, "createdAt");
        ObjectPrecondition.requireNonNull(expiresAt, "expiresAt");
    }

    public static SessionContext of(UUID sessionId, Instant createdAt, Instant expiresAt, @Nullable Instant revokedAt) {
        return new SessionContext(sessionId, createdAt, expiresAt, revokedAt);
    }
}
