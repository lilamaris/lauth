package com.lilamaris.lauth.identity.application.port.out;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public interface UserGrantStore {
    boolean grantAll(UUID userId, Set<UUID> scopeIds, Instant createdAt);
}
