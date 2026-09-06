package com.lilamaris.lauth.identity.jdbc;

import com.lilamaris.lauth.identity.application.port.out.UserGrantStore;
import com.lilamaris.lauth.identity.jdbc.sql.UserGrantSql;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserGrantJdbcAdapter implements UserGrantStore {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public boolean grantAll(UUID userId, Set<UUID> scopeIds, Instant createdAt) {
        var sql = UserGrantSql.INSERT_USER_GRANT;
        var args = scopeIds.stream()
                .map(
                        scope -> new MapSqlParameterSource()
                                .addValue("userId", userId)
                                .addValue("scopeId", scopeIds)
                                .addValue("createdAt", Timestamp.from(createdAt))
                )
                .toArray(SqlParameterSource[]::new);

        var updated = jdbcTemplate.batchUpdate(sql, args);

        return updated.length == scopeIds.size()
                && Arrays.stream(updated).allMatch(e -> e == 1 || e == Statement.SUCCESS_NO_INFO);
    }
}

