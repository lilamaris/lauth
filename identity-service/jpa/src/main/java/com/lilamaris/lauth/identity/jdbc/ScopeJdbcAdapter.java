package com.lilamaris.lauth.identity.jdbc;

import com.lilamaris.lauth.identity.application.port.out.ScopeStore;
import com.lilamaris.lauth.identity.domain.scope.Scope;
import com.lilamaris.lauth.identity.jdbc.sql.ScopeSql;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ScopeJdbcAdapter implements ScopeStore {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public boolean saveAll(Set<Scope> scopes) {
        var sql = ScopeSql.INSERT;
        var args = scopes.stream()
                .map(scope -> new MapSqlParameterSource()
                        .addValue("resource", scope.getResource())
                        .addValue("action", scope.getAction().canonicalName())
                        .addValue("description", scope.getDescription())
                        .addValue("createdAt", Timestamp.from(scope.getCreatedAt()))
                )
                .toArray(SqlParameterSource[]::new);

        var result = jdbcTemplate.batchUpdate(sql, args);

        return result.length == scopes.size()
                && Arrays.stream(result).allMatch(e -> e == 1 || e == Statement.SUCCESS_NO_INFO);
    }
}
