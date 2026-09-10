package com.lilamaris.lauth.identity.application.model.scope;

import com.lilamaris.lauth.kernel.core.condition.CollectionPrecondition;
import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;

import java.util.Set;
import java.util.UUID;

public record GrantedScope(
        UUID userId,
        Set<ResourceScope> scopes
) {
    public GrantedScope {
        ObjectPrecondition.requireNonNull(userId, "userId");
        scopes = Set.copyOf(CollectionPrecondition.requireNonNullElements(scopes, "scopes"));
    }

    public static GrantedScope of(UUID userId, Set<ResourceScope> scopes) {
        return new GrantedScope(userId, scopes);
    }
}
