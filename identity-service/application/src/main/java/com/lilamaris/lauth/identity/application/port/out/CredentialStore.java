package com.lilamaris.lauth.identity.application.port.out;

import java.time.Instant;
import java.util.UUID;

public interface CredentialStore {
    boolean save(UUID userId, String email, String passwordHash, Instant createdAt);

    boolean updatePasswordHash(UUID id, String passwordHash, Instant updatedAt);
}
