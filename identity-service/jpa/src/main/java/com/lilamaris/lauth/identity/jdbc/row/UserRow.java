package com.lilamaris.lauth.identity.jdbc.row;

import com.lilamaris.lauth.identity.application.model.scope.ResourceScope;
import com.lilamaris.lauth.identity.domain.scope.Action;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class UserRow {
    public record Principal(
            UUID userId,
            String displayName,
            Instant createdAt,
            Instant updatedAt,
            @Nullable String resource,
            @Nullable String actionStr
    ) {
        public Optional<ResourceScope> toResourceScope() {
            if (resource == null || actionStr == null) return Optional.empty();
            return Optional.of(
                    ResourceScope.of(resource, Action.from(actionStr))
            );
        }
    }
}
