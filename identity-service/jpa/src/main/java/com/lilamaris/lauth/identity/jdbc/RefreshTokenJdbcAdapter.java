package com.lilamaris.lauth.identity.jdbc;

import com.lilamaris.lauth.identity.application.model.session.RefreshTokenContext;
import com.lilamaris.lauth.identity.application.port.out.RefreshTokenContextReader;
import com.lilamaris.lauth.identity.application.port.out.RefreshTokenStore;
import com.lilamaris.lauth.identity.domain.RefreshToken;
import com.lilamaris.lauth.identity.jdbc.row.RefreshTokenRow;
import com.lilamaris.lauth.identity.jdbc.sql.RefreshTokenSql;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenJdbcAdapter implements RefreshTokenStore, RefreshTokenContextReader {
    private final JdbcClient jdbcClient;

    @Override
    public UUID save(RefreshToken refreshToken) {
        var sql = RefreshTokenSql.INSERT;

        var consumedAt = Optional.ofNullable(refreshToken.getConsumedAt())
                .map(Timestamp::from)
                .orElse(null);

        return jdbcClient.sql(sql)
                .param("sessionId", refreshToken.getSessionId())
                .param("tokenHash", refreshToken.getTokenHash())
                .param("issuedAt", Timestamp.from(refreshToken.getIssuedAt()))
                .param("expiresAt", Timestamp.from(refreshToken.getExpiresAt()))
                .param("consumedAt", consumedAt)
                .query(UUID.class)
                .single();
    }

    @Override
    public boolean tryConsume(UUID refreshTokenId, Instant consumedAt) {
        return jdbcClient.sql(RefreshTokenSql.TRY_CONSUME)
                .param("refreshTokenId", refreshTokenId)
                .param("consumedAt", Timestamp.from(consumedAt))
                .update() == 1;
    }

    @Override
    public Optional<RefreshTokenContext> findByRefreshTokenId(UUID refreshTokenId) {
        return jdbcClient.sql(RefreshTokenSql.FIND_CONTEXT_BY_ID)
                .param("refreshTokenId", refreshTokenId)
                .query(RefreshTokenRow.Context.class)
                .optional()
                .map(RefreshTokenRow.Context::toContext);
    }
}
