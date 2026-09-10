package com.lilamaris.lauth.identity.jdbc;

import com.lilamaris.lauth.identity.application.port.out.SessionStore;
import com.lilamaris.lauth.identity.domain.Session;
import com.lilamaris.lauth.identity.jdbc.sql.SessionSql;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SessionJdbcAdapter implements SessionStore {
    private final JdbcClient jdbcClient;

    @Override
    public UUID save(Session session) {
        var sql = SessionSql.INSERT;

        var revokedAt = Optional.ofNullable(session.getRevokedAt())
                .map(Timestamp::from)
                .orElse(null);

        return jdbcClient.sql(sql)
                .param("userId", session.getUserId())
                .param("device", session.getDevice())
                .param("createdAt", Timestamp.from(session.getCreatedAt()))
                .param("expiresAt", Timestamp.from(session.getExpiresAt()))
                .param("lastUsedAt", Timestamp.from(session.getLastUsedAt()))
                .param("revokedAt", revokedAt)
                .query(UUID.class)
                .single();
    }

    @Override
    public boolean tryRevoke(UUID sessionId, Instant revokedAt) {
        return jdbcClient.sql(SessionSql.REVOKE)
                .param("sessionId", sessionId)
                .param("revokedAt", Timestamp.from(revokedAt))
                .update() == 1;
    }
}
