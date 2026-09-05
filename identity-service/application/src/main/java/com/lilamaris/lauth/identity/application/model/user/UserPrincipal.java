package com.lilamaris.lauth.identity.application.model.user;

import com.lilamaris.cozyr.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.cozyr.kernel.core.condition.StringPrecondition;
import com.lilamaris.lauth.identity.application.model.scope.GrantedScope;

import java.time.Instant;
import java.util.UUID;

public record UserPrincipal(
        UUID userId,
        String displayName,
        Instant createdAt,
        Instant updatedAt,
        GrantedScope scopes
) {
    public UserPrincipal {
        ObjectPrecondition.requireNonNull(userId, "userId");
        StringPrecondition.requireNonBlank(displayName, "displayName");
        ObjectPrecondition.requireNonNull(createdAt, "createdAt");
        ObjectPrecondition.requireNonNull(updatedAt, "updatedAt");
        ObjectPrecondition.requireNonNull(scopes, "scopes");
    }

    public static UserPrincipal of(UUID userId, String displayName, Instant createdAt, Instant updatedAt, GrantedScope scopes) {
        return new UserPrincipal(userId, displayName, createdAt, updatedAt, scopes);
    }
}
