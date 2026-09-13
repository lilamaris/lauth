package com.lilamaris.lauth.identity.jdbc.sql;

public class FederatedAccountSql {
    public static final String INSERT = """
            INSERT INTO federated_account (
                user_id,
                registration_id,
                provider_user_id,
                created_at
            ) VALUES (
                :userId,
                :registrationId,
                :providerUserId,
                :createdAt
            )
            ON CONFLICT (registration_id, provider_user_id)
            DO NOTHING
            """;

    public static final String FIND_USER_PRINCIPAL_BY_REGISTRATION_ID = """
            SELECT
                u.id AS userId,
                u.display_name AS displayName,
                u.created_at AS createdAt,
                u.updated_at AS updatedAt
            FROM federated_account f
            JOIN service_user u
                ON u.id = f.user_id
            WHERE f.registration_id = :registrationId
                AND f.provider_user_id = :providerUserId
            """;
}
