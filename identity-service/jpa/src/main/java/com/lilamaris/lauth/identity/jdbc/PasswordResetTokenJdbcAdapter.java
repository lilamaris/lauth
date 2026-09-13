package com.lilamaris.lauth.identity.jdbc;

import com.lilamaris.lauth.identity.application.port.out.PasswordResetTokenStore;
import com.lilamaris.lauth.identity.domain.PasswordResetToken;
import com.lilamaris.lauth.identity.jdbc.sql.PasswordResetTokenSql;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PasswordResetTokenJdbcAdapter implements PasswordResetTokenStore {
    private final JdbcClient jdbcClient;

    @Override
    public boolean save(PasswordResetToken passwordResetToken) {
        var sql = PasswordResetTokenSql.INSERT;
        var revokedAt = Optional.ofNullable(passwordResetToken.getRevokedAt())
                .map(Timestamp::from)
                .orElse(null);
        var consumedAt = Optional.ofNullable(passwordResetToken.getConsumedAt())
                .map(Timestamp::from)
                .orElse(null);
        return jdbcClient.sql(sql)
                .param("id", passwordResetToken.getId())
                .param("credentialId", passwordResetToken.getCredentialId())
                .param("clientId", passwordResetToken.getClientId())
                .param("tokenHash", passwordResetToken.getTokenHash())
                .param("issuedAt", Timestamp.from(passwordResetToken.getIssuedAt()))
                .param("expiresAt", Timestamp.from(passwordResetToken.getExpiresAt()))
                .param("revokedAt", revokedAt)
                .param("consumedAt", consumedAt)
                .update() == 1;
    }

    @Override
    public boolean revokeOpenByCredentialId(UUID credentialId, Instant revokedAt) {
        var sql = PasswordResetTokenSql.REVOKE_OPEN_TOKEN;
        return jdbcClient.sql(sql)
                .param("credentialId", credentialId)
                .param("revokedAt", Timestamp.from(revokedAt))
                .update() > 0;
    }

    @Override
    public boolean consume(UUID passwordResetTokenId, Instant consumedAt) {
        var sql = PasswordResetTokenSql.CONSUME_TOKEN;
        return jdbcClient.sql(sql)
                .param("id", passwordResetTokenId)
                .param("consumedAt", Timestamp.from(consumedAt))
                .update() == 1;
    }
}
