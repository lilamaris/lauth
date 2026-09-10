package com.lilamaris.lauth.identity.jdbc.row;

import com.lilamaris.lauth.identity.application.model.scope.GrantedScope;
import com.lilamaris.lauth.identity.application.model.scope.ResourceScope;
import com.lilamaris.lauth.identity.application.model.session.RefreshTokenContext;
import com.lilamaris.lauth.identity.application.model.session.SessionContext;
import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.domain.scope.Action;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
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
            Instant userUpdatedAt,
            @Nullable String resource,
            @Nullable String actionStr
    ) {
        public Optional<ResourceScope> toResourceScope() {
            if (resource == null || actionStr == null) return Optional.empty();
            return Optional.of(ResourceScope.of(resource, Action.from(actionStr)));
        }

        public RefreshTokenContext toContext(Set<ResourceScope> scopes) {
            var user = UserPrincipal.of(userId, displayName, userCreatedAt, userUpdatedAt,
                    GrantedScope.of(userId, scopes));
            var session = SessionContext.of(sessionId, sessionCreatedAt, sessionExpiresAt, revokedAt);
            return RefreshTokenContext.of(refreshTokenId, tokenHash, tokenExpiresAt, consumedAt, user, session);
        }
    }
}
