package com.lilamaris.lauth.identity.jdbc;

import com.lilamaris.lauth.identity.application.port.out.RefreshTokenStore;
import com.lilamaris.lauth.identity.domain.RefreshToken;
import com.lilamaris.lauth.identity.jdbc.sql.RefreshTokenSql;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenJdbcAdapter implements RefreshTokenStore {
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
}
