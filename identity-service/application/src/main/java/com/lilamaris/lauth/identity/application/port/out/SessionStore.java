package com.lilamaris.lauth.identity.application.port.out;

import com.lilamaris.lauth.identity.domain.Session;

import java.time.Instant;
import java.util.UUID;

public interface SessionStore {
    UUID save(Session session);

    boolean tryRevoke(UUID sessionId, Instant revokedAt);
}
