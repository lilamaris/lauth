package com.lilamaris.lauth.identity.jdbc.sql;

public class SessionSql {
    public static final String REVOKE = """
            UPDATE user_session
            SET revoked_at = :revokedAt
            WHERE id = :sessionId AND revoked_at IS NULL
            """;

    public static final String INSERT = """
            INSERT INTO user_session (
                user_id,
                device,
                created_at,
                expires_at,
                last_used_at,
                revoked_at
            ) VALUES (
                :userId,
                :device,
                :createdAt,
                :expiresAt,
                :lastUsedAt,
                :revokedAt
            )
            RETURNING id
            """;
}
