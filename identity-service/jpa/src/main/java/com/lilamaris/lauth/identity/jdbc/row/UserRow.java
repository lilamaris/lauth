package com.lilamaris.lauth.identity.jdbc.row;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;

import java.time.Instant;
import java.util.UUID;

public class UserRow {
    public record Principal(
            UUID userId,
            String displayName,
            Instant createdAt,
            Instant updatedAt
    ) {
        public UserPrincipal toModel() {
            return UserPrincipal.of(userId, displayName, createdAt, updatedAt);
        }
    }
}
