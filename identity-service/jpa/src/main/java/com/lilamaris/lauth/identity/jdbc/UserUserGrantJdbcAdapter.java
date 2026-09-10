package com.lilamaris.lauth.identity.jdbc;

import com.lilamaris.lauth.identity.application.model.scope.ResourceScope;
import com.lilamaris.lauth.identity.application.port.out.UserGrantReader;
import com.lilamaris.lauth.identity.application.port.out.UserGrantStore;
import com.lilamaris.lauth.identity.jdbc.row.ScopeRow;
import com.lilamaris.lauth.identity.jdbc.sql.UserGrantSql;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserUserGrantJdbcAdapter implements UserGrantStore, UserGrantReader {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JdbcClient jdbcClient;

    @Override
    public boolean grantAll(UUID userId, Set<UUID> scopeIds, Instant createdAt) {
        var sql = UserGrantSql.INSERT_USER_GRANT;
        var args = scopeIds.stream()
                .map(
                        scopeId -> new MapSqlParameterSource()
                                .addValue("userId", userId)
                                .addValue("scopeId", scopeId)
                                .addValue("createdAt", Timestamp.from(createdAt))
                )
                .toArray(SqlParameterSource[]::new);

        var updated = jdbcTemplate.batchUpdate(sql, args);

        return updated.length == scopeIds.size()
                && Arrays.stream(updated).allMatch(e -> e == 1 || e == Statement.SUCCESS_NO_INFO);
    }

    @Override
    public Set<ResourceScope> findByUserId(UUID userId) {
        var sql = UserGrantSql.FIND_GRANT_FROM_USER_ID;
        return jdbcClient.sql(sql)
                .param("userId", userId)
                .query(ScopeRow.class)
                .stream()
                .filter(Objects::nonNull)
                .map(ScopeRow::toModel)
                .collect(Collectors.toUnmodifiableSet());
    }
}

