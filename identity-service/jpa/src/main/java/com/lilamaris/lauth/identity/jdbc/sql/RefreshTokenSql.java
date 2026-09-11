package com.lilamaris.lauth.identity.jdbc.sql;

public class RefreshTokenSql {
    public static final String TRY_CONSUME = """
            UPDATE refresh_token
            SET consumed_at = :consumedAt
            WHERE id = :refreshTokenId
                AND consumed_at IS NULL
                AND expires_at > :consumedAt
            """;

    public static final String FIND_CONTEXT_BY_ID = """
            SELECT
                s.user_id AS userId,
                t.id AS refreshTokenId,
                t.token_hash AS tokenHash,
                t.expires_at AS tokenExpiresAt,
                t.consumed_at AS consumedAt,
                s.id AS sessionId,
                s.created_at AS sessionCreatedAt,
                s.expires_at AS sessionExpiresAt,
                s.revoked_at AS revokedAt
            FROM refresh_token t
            JOIN user_session s ON s.id = t.session_id
            WHERE t.id = :refreshTokenId
            """;

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
