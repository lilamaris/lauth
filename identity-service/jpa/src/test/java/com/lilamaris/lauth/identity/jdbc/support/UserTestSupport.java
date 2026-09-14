package com.lilamaris.lauth.identity.jdbc.support;

import org.springframework.jdbc.core.simple.JdbcClient;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public class UserTestSupport {
    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String INSERT_USER = """
            INSERT INTO service_user (id, display_name, created_at, updated_at)
            VALUES (:id, 'test-user-1', :now, :now)
            RETURNING id
            """;

    private static final String CLEANUP_USER = """
            DELETE FROM service_user WHERE id = :userId
            """;

    public static TestContext createContext(JdbcClient jdbcClient, Instant now) {
        var userId = jdbcClient.sql(INSERT_USER)
                .param("id", USER_ID)
                .param("now", Timestamp.from(now))
                .query(UUID.class)
                .single();

        return new TestContext(userId);
    }

    public static void cleanup(JdbcClient jdbcClient) {
        jdbcClient.sql(CLEANUP_USER)
                .param("userId", USER_ID)
                .update();
    }

    public record TestContext(UUID userId) {
    }
}
