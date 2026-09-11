package com.lilamaris.lauth.identity.jdbc.sql;

public class UserSql {
    public static final String INSERT = """
            INSERT INTO service_user (
                display_name,
                created_at,
                updated_at
            ) VALUES (
                :displayName,
                :createdAt,
                :updatedAt
            )
            RETURNING id
            """;

    public static final String FIND_PRINCIPAL_BY_ID = """
            SELECT
                u.id AS userId,
                u.display_name AS displayName,
                u.created_at AS createdAt,
                u.updated_at AS updatedAt
            FROM service_user u
            WHERE u.id = :userId
            """;

    public static final String UPDATE_DISPLAY_NAME = """
            UPDATE service_user
            SET display_name = :displayName,
                updated_at = :updatedAt
            WHERE id = :userId
            """;

    public static final String UPDATE_HANDLE = """
            UPDATE service_user
            SET handle = :handle,
                updated_at = :updatedAt
            WHERE id = :userId
            """;
}
