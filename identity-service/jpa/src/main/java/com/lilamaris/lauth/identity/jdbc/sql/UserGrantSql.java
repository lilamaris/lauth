package com.lilamaris.lauth.identity.jdbc.sql;

public class UserGrantSql {
    public final static String INSERT_USER_GRANT = """
            INSERT INTO user_grant (
                user_id,
                scope_id,
                created_at
            ) VALUES (
                :userId,
                :scopeId,
                :createdAt
            )
            """;

    public final static String FIND_GRANT_FROM_USER_ID = """
            SELECT
                s.resource AS resource,
                s.action AS action
            FROM user_grant g
            JOIN scope s
                ON s.id = g.scope_id
            WHERE g.user_id = :userId
            """;
}
