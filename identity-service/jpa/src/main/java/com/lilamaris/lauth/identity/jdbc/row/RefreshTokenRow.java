package com.lilamaris.lauth.identity.jdbc.row;

import com.lilamaris.lauth.identity.application.model.session.RefreshTokenContext;
import com.lilamaris.lauth.identity.application.model.session.SessionContext;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

public class RefreshTokenRow {
    public record Context(
            UUID userId,
            UUID refreshTokenId,
            String tokenHash,
            Instant tokenExpiresAt,
            @Nullable Instant consumedAt,
            UUID sessionId,
            Instant sessionCreatedAt,
            Instant sessionExpiresAt,
            @Nullable Instant revokedAt
    ) {
        public RefreshTokenContext toContext() {
            var session = SessionContext.of(sessionId, sessionCreatedAt, sessionExpiresAt, revokedAt);
            return RefreshTokenContext.of(userId, refreshTokenId, tokenHash, tokenExpiresAt, consumedAt, session);
        }
    }
}
