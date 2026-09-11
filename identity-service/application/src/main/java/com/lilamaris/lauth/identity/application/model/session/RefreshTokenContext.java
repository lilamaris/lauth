package com.lilamaris.lauth.identity.application.model.session;

import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

public record RefreshTokenContext(
        UUID userId,
        UUID refreshTokenId,
        String tokenHash,
        Instant expiresAt,
        @Nullable Instant consumedAt,
        SessionContext session
) {
    public RefreshTokenContext {
        ObjectPrecondition.requireNonNull(userId, "userId");
        ObjectPrecondition.requireNonNull(refreshTokenId, "refreshTokenId");
        StringPrecondition.requireNonBlank(tokenHash, "tokenHash");
        ObjectPrecondition.requireNonNull(expiresAt, "expiresAt");
        ObjectPrecondition.requireNonNull(session, "session");
    }

    public static RefreshTokenContext of(UUID userId, UUID refreshTokenId, String tokenHash, Instant expiresAt, @Nullable Instant consumedAt, SessionContext session) {
        return new RefreshTokenContext(userId, refreshTokenId, tokenHash, expiresAt, consumedAt, session);
    }
}
