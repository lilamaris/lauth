package com.lilamaris.lauth.identity.application.model.session;

import com.lilamaris.cozyr.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.cozyr.kernel.core.condition.StringPrecondition;
import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

public record RefreshTokenContext(
        UUID refreshTokenId,
        String tokenHash,
        Instant expiresAt,
        @Nullable Instant consumedAt,
        UserPrincipal user,
        SessionContext session
) {
    public RefreshTokenContext {
        ObjectPrecondition.requireNonNull(refreshTokenId, "refreshTokenId");
        StringPrecondition.requireNonBlank(tokenHash, "tokenHash");
        ObjectPrecondition.requireNonNull(expiresAt, "expiresAt");
        ObjectPrecondition.requireNonNull(user, "user");
        ObjectPrecondition.requireNonNull(session, "session");
    }

    public static RefreshTokenContext of(UUID refreshTokenId, String tokenHash, Instant expiresAt, @Nullable Instant consumedAt, UserPrincipal user, SessionContext session) {
        return new RefreshTokenContext(refreshTokenId, tokenHash, expiresAt, consumedAt, user, session);
    }
}
