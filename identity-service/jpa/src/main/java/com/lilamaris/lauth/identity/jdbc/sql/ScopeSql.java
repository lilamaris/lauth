package com.lilamaris.lauth.identity.jdbc.sql;

public class ScopeSql {
    public static final String INSERT = """
            INSERT INTO scope (
                resource,
                action,
                description,
                created_at
            ) VALUES (
                :resource,
                :action,
                :description,
                :createdAt
            )
            ON CONFLICT DO NOTHING
            """;
}
