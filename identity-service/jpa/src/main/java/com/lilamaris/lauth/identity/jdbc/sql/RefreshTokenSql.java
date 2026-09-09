package com.lilamaris.lauth.identity.jdbc.sql;

public class RefreshTokenSql {
    public static final String INSERT = """
            INSERT INTO refresh_token (
                session_id,
                token_hash,
                issued_at,
                expires_at,
                consumed_at
            ) VALUES (
                :sessionId,
                :tokenHash,
                :issuedAt,
                :expiresAt,
                :consumedAt
            )
            RETURNING id
            """;
}
