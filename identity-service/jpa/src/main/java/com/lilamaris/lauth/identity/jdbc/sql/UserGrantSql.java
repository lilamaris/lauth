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
}
