package com.lilamaris.lauth.identity.jdbc.sql;

public class SessionSql {
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
