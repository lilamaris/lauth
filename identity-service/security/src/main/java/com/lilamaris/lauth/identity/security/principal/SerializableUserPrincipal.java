package com.lilamaris.lauth.identity.security.principal;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record SerializableUserPrincipal(
         UUID userId,
         String displayName,
         Instant createdAt,
         Instant updatedAt
) implements Serializable {
    public SerializableUserPrincipal {
        ObjectPrecondition.requireNonNull(userId, "userId");
        StringPrecondition.requireNonBlank(displayName, "displayName");
        ObjectPrecondition.requireNonNull(createdAt, "createdAt");
        ObjectPrecondition.requireNonNull(updatedAt, "updatedAt");
    }

    public static SerializableUserPrincipal of(UUID userId, String displayName, Instant createdAt, Instant updatedAt) {
        return new SerializableUserPrincipal(userId, displayName, createdAt, updatedAt);
    }

    public static SerializableUserPrincipal from(UserPrincipal userPrincipal) {
        return new SerializableUserPrincipal(userPrincipal.userId(), userPrincipal.displayName(), userPrincipal.createdAt(), userPrincipal.updatedAt());
    }
}
