package com.lilamaris.lauth.identity.jdbc;

import com.lilamaris.lauth.identity.application.model.credential.CredentialChallenge;
import com.lilamaris.lauth.identity.application.port.out.CredentialAuthReader;
import com.lilamaris.lauth.identity.application.port.out.CredentialStore;
import com.lilamaris.lauth.identity.domain.Credential;
import com.lilamaris.lauth.identity.jdbc.row.CredentialRow;
import com.lilamaris.lauth.identity.jdbc.sql.CredentialSql;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CredentialJdbcAdapter implements CredentialStore, CredentialAuthReader {
    private final JdbcClient jdbcClient;

    @Override
    public boolean save(UUID userId, String email, String passwordHash, Instant createdAt) {
        var credential = Credential.of(userId, email, passwordHash, createdAt);
        var sql = CredentialSql.INSERT_SINGLE;

        try {
            var updateCount = jdbcClient.sql(sql)
                    .param("userId", credential.getUserId())
                    .param("email", credential.getEmail())
                    .param("passwordHash", credential.getPasswordHash())
                    .param("createdAt", Timestamp.from(credential.getCreatedAt()))
                    .param("updatedAt", Timestamp.from(credential.getUpdatedAt()))
                    .update();

            return updateCount > 0;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    @Override
    public Optional<CredentialChallenge> findByEmail(String email) {
        var sql = CredentialSql.FIND_CHALLENGE_BY_EMAIL;

        return jdbcClient.sql(sql)
                .param("email", email)
                .query(CredentialRow.Challenge.class)
                .optional()
                .map(CredentialRow.Challenge::toModel);
    }
}
