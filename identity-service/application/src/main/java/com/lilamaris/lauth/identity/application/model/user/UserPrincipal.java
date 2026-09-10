package com.lilamaris.lauth.identity.application.model.user;

import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;

import java.time.Instant;
import java.util.UUID;

public record UserPrincipal(
        UUID userId,
        String displayName,
        Instant createdAt,
        Instant updatedAt
) {
    public UserPrincipal {
        ObjectPrecondition.requireNonNull(userId, "userId");
        StringPrecondition.requireNonBlank(displayName, "displayName");
        ObjectPrecondition.requireNonNull(createdAt, "createdAt");
        ObjectPrecondition.requireNonNull(updatedAt, "updatedAt");
    }

    public static UserPrincipal of(UUID userId, String displayName, Instant createdAt, Instant updatedAt) {
        return new UserPrincipal(userId, displayName, createdAt, updatedAt);
    }
}
