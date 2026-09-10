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
                t.id AS refreshTokenId,
                t.token_hash AS tokenHash,
                t.expires_at AS tokenExpiresAt,
                t.consumed_at AS consumedAt,
                s.id AS sessionId,
                s.created_at AS sessionCreatedAt,
                s.expires_at AS sessionExpiresAt,
                s.revoked_at AS revokedAt,
                u.id AS userId,
                u.display_name AS displayName,
                u.created_at AS userCreatedAt,
                u.updated_at AS userUpdatedAt,
                p.resource AS resource,
                p.action AS actionStr
            FROM refresh_token t
            JOIN user_session s ON s.id = t.session_id
            JOIN service_user u ON u.id = s.user_id
            LEFT JOIN user_grant g ON g.user_id = u.id
            LEFT JOIN scope p ON p.id = g.scope_id
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
