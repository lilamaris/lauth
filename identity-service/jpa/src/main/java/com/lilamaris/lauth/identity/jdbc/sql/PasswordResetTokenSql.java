package com.lilamaris.lauth.identity.jdbc.sql;

public class PasswordResetTokenSql {
    public static final String INSERT = """
            INSERT INTO password_reset_token (
                credential_id,
                token_hash,
                issued_at,
                expires_at,
                revoked_at,
                consumed_at
            ) VALUES (
                :credentialId,
                :tokenHash,
                :issuedAt,
                :expiresAt,
                :revokedAt,
                :consumedAt
            )
            ON CONFLICT (credential_id)
            WHERE consumed_at IS NULL
                AND revoked_at IS NULL
            DO NOTHING
            RETURNING id
            """;

    public static final String REVOKE_OPEN_TOKEN = """
            UPDATE password_reset_token
            SET revoked_at = :revokedAt
            WHERE credential_id = :credentialId
                AND revoked_at IS NULL
                AND consumed_at IS NULL
            """;
}
