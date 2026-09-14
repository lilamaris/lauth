package com.lilamaris.lauth.identity.jdbc.assertion;

import org.springframework.jdbc.core.simple.JdbcClient;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SessionAssertion {
    private static final String GET_REVOKED_AT = """
                SELECT revoked_at
                FROM user_session
                WHERE id = :id
            """;
    private static final String GET_CONSUMED_AT = """
            SELECT consumed_at
            FROM refresh_token
            WHERE id = :id
            """;

    public static void assertRevokedAt(JdbcClient jdbcClient, UUID sessionId, Instant revokedAt) {
        assertThat(getRevokedAt(jdbcClient, sessionId)).contains(revokedAt);
    }

    public static void assertNotRevoked(JdbcClient jdbcClient, UUID sessionId) {
        assertThat(getRevokedAt(jdbcClient, sessionId)).isEmpty();
    }

    public static void assertConsumedAt(JdbcClient jdbcClient, UUID refreshTokenId, Instant consumedAt) {
        assertThat(getConsumedAt(jdbcClient, refreshTokenId)).contains(consumedAt);
    }

    public static void assertNotConsumed(JdbcClient jdbcClient, UUID refreshTokenId) {
        assertThat(getConsumedAt(jdbcClient, refreshTokenId)).isEmpty();
    }

    private static Optional<Instant> getRevokedAt(JdbcClient jdbcClient, UUID sessionId) {
        return jdbcClient.sql(GET_REVOKED_AT)
                .param("id", sessionId)
                .query((rs, rowNum) -> Optional.ofNullable(rs.getTimestamp("revoked_at"))
                        .map(Timestamp::toInstant)
                )
                .single();
    }

    private static Optional<Instant> getConsumedAt(JdbcClient jdbcClient, UUID refreshTokenId) {
        return jdbcClient.sql(GET_CONSUMED_AT)
                .param("id", refreshTokenId)
                .query((rs, rowNum) -> Optional.ofNullable(rs.getTimestamp("consumed_at"))
                        .map(Timestamp::toInstant)
                )
                .single();
    }
}
