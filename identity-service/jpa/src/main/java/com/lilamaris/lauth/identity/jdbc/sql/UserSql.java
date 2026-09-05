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
            WITH granted_scope AS (
                SELECT
                    s.resource,
                    s.action
                FROM user_grant g
                JOIN scope s
                    ON s.id = g.scope_id
                WHERE g.user_id = :userId
            )
            SELECT
                u.id AS userId,
                u.display_name AS displayName,
                u.created_at AS createdAt,
                u.updated_at AS updatedAt,
                g.resource AS resource,
                g.action AS action
            FROM service_user u
            LEFT JOIN granted_scope g
                ON g.user_id = u.id
            WHERE u.id = :userId
            """;
}
