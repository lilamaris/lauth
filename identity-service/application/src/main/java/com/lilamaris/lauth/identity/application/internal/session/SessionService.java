package com.lilamaris.lauth.identity.application.internal.session;

import com.lilamaris.lauth.identity.application.config.session.SessionProperties;
import com.lilamaris.lauth.identity.application.model.session.SessionContext;
import com.lilamaris.lauth.identity.application.port.out.SessionStore;
import com.lilamaris.lauth.identity.domain.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SessionService {
    private final SessionProperties sessionProperties;
    private final SessionStore sessionStore;

    public SessionContext create(UUID userId, Instant createdAt) {
        var expiresAt = createdAt.plus(sessionProperties.expiration());
        var session = Session.of(userId, "unknown", createdAt, expiresAt);
        var sessionId = sessionStore.save(session);

        return SessionContext.of(sessionId, session.getCreatedAt(), session.getExpiresAt(), session.getRevokedAt());
    }
}
