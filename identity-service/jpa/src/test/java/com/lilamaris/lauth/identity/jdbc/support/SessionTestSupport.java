package com.lilamaris.lauth.identity.jdbc.support;

import com.lilamaris.lauth.identity.application.model.opaque.OpaqueToken;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class SessionTestSupport {
    private static final UUID TARGET_SESSION_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID SECOND_SESSION_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID REFRESH_TOKEN_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final Duration SESSION_EXPIRATION = Duration.ofDays(7);
    private static final Duration REFRESH_TOKEN_EXPIRATION = Duration.ofSeconds(30);
    private static final String TOKEN = "refresh-token";
    private static final String TOKEN_HASH = "hash:" + TOKEN;
    private static final String INSERT_SESSION = """
            INSERT INTO user_session (id, user_id, device, created_at, last_used_at, expires_at, revoked_at)
            VALUES (:id, :userId, 'some-device', :now, :now, :expiresAt, NULL)
            RETURNING id
            """;
    private static final String INSERT_REFRESH_TOKEN = """
            INSERT INTO refresh_token (id, session_id, token_hash, issued_at, expires_at, consumed_at)
            VALUES (:id, :sessionId, :tokenHash, :now, :expiresAt, NULL)
            RETURNING id
            """;
    private static final String CLEANUP_SESSION = """
            DELETE FROM user_session WHERE user_id = :userId
            """;
    private static final String CLEANUP_REFRESH_TOKEN = """
            DELETE FROM refresh_token
            WHERE session_id IN (
                SELECT s.id
                FROM user_session s
                WHERE s.user_id = :userId
            )
            """;

    public static TestContext createContext(JdbcClient jdbcClient, UUID userId, Instant now) {
        var targetSessionId = createSession(jdbcClient, TARGET_SESSION_ID, userId, now, now.plus(SESSION_EXPIRATION));
        var secondSessionId = createSession(jdbcClient, SECOND_SESSION_ID, userId, now, now.plus(SESSION_EXPIRATION));
        var refreshTokenId = createRefreshToken(jdbcClient, REFRESH_TOKEN_ID, TARGET_SESSION_ID, TOKEN_HASH, now, now.plus(REFRESH_TOKEN_EXPIRATION));
        var opaqueToken = OpaqueToken.of(refreshTokenId.toString(), TOKEN);

        return new TestContext(targetSessionId, secondSessionId, refreshTokenId, opaqueToken);
    }

    public static void cleanup(JdbcClient jdbcClient, UUID userId) {
        jdbcClient.sql(CLEANUP_REFRESH_TOKEN).param("userId", userId).update();

        jdbcClient.sql(CLEANUP_SESSION).param("userId", userId).update();
    }

    private static UUID createSession(JdbcClient jdbcClient, UUID sessionId, UUID userId, Instant now, Instant expiresAt) {
        return jdbcClient.sql(INSERT_SESSION).param("id", sessionId).param("userId", userId).param("now", Timestamp.from(now)).param("expiresAt", Timestamp.from(expiresAt)).query(UUID.class).single();
    }

    private static UUID createRefreshToken(JdbcClient jdbcClient, UUID refreshTokenId, UUID sessionId, String tokenHash, Instant now, Instant expiresAt) {
        return jdbcClient.sql(INSERT_REFRESH_TOKEN).param("id", refreshTokenId).param("sessionId", sessionId).param("tokenHash", tokenHash).param("now", Timestamp.from(now)).param("expiresAt", Timestamp.from(expiresAt)).query(UUID.class).single();
    }

    public record TestContext(UUID targetSessionId, UUID secondSessionId, UUID refreshTokenId,
                              OpaqueToken opaqueToken) {
    }
}
