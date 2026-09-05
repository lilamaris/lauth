package com.lilamaris.lauth.identity.jdbc.row;

import com.lilamaris.lauth.identity.application.model.scope.ResourceScope;
import com.lilamaris.lauth.identity.domain.scope.Action;

import java.time.Instant;
import java.util.UUID;

public class UserRow {
    public record Principal(
            UUID userId,
            String displayName,
            Instant createdAt,
            Instant updatedAt,
            String resource,
            String actionStr
    ) {
        public ResourceScope toResourceScope() {
            return ResourceScope.of(resource, Action.from(actionStr));
        }
    }
}
