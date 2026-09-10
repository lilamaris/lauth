package com.lilamaris.lauth.identity.application.port.in.result;

import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;

import java.util.UUID;

public record SessionResult(
        UUID sessionId,
        UUID userId
) {
    public SessionResult {
        ObjectPrecondition.requireNonNull(sessionId, "sessionId");
        ObjectPrecondition.requireNonNull(userId, "userId");
    }

    public static SessionResult of(UUID sessionId, UUID userId) {
        return new SessionResult(sessionId, userId);
    }
}
