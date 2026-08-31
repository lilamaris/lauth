package com.lilamaris.lauth.identity.jdbc.sql;

public class CredentialSql {
    public static final String INSERT_SINGLE = """
            INSERT INTO credential (
                user_id,
                email,
                password_hash,
                created_at,
                updated_at
            ) VALUES (
                :userId,
                :email,
                :passwordHash,
                :createdAt,
                :updatedAt
            )
            """;

    public static final String FIND_CHALLENGE_BY_EMAIL = """
            SELECT
                u.id AS userId,
                c.email AS email,
                c.password_hash AS passwordHash
            FROM credential c
            JOIN service_user u
                ON u.id = c.user_id
            WHERE c.email = :email
            """;
}
