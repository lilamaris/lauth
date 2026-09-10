package com.lilamaris.lauth.identity.jdbc.row;

import com.lilamaris.lauth.identity.application.model.session.RefreshTokenContext;
import com.lilamaris.lauth.identity.application.model.session.SessionContext;
import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

public class RefreshTokenRow {
    public record Context(
            UUID refreshTokenId,
            String tokenHash,
            Instant tokenExpiresAt,
            @Nullable Instant consumedAt,
            UUID sessionId,
            Instant sessionCreatedAt,
            Instant sessionExpiresAt,
            @Nullable Instant revokedAt,
            UUID userId,
            String displayName,
            Instant userCreatedAt,
            Instant userUpdatedAt
    ) {
        public RefreshTokenContext toContext() {
            var user = UserPrincipal.of(userId, displayName, userCreatedAt, userUpdatedAt);
            var session = SessionContext.of(sessionId, sessionCreatedAt, sessionExpiresAt, revokedAt);
            return RefreshTokenContext.of(refreshTokenId, tokenHash, tokenExpiresAt, consumedAt, user, session);
        }
    }
}
