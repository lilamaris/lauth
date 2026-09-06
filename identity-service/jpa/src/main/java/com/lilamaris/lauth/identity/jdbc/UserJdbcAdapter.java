package com.lilamaris.lauth.identity.jdbc;

import com.lilamaris.lauth.identity.application.model.scope.GrantedScope;
import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.out.UserPrincipalReader;
import com.lilamaris.lauth.identity.application.port.out.UserStore;
import com.lilamaris.lauth.identity.domain.User;
import com.lilamaris.lauth.identity.jdbc.row.UserRow;
import com.lilamaris.lauth.identity.jdbc.sql.UserSql;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserJdbcAdapter implements UserStore, UserPrincipalReader {
    private final JdbcClient jdbcClient;

    @Override
    public Optional<UserPrincipal> findPrincipalById(UUID userId) {
        var sql = UserSql.FIND_PRINCIPAL_BY_ID;

        var rows = jdbcClient.sql(sql)
                .param("userId", userId)
                .query(UserRow.Principal.class)
                .list();

        if (rows.isEmpty()) return Optional.empty();

        var first = rows.getFirst();

        if (first == null) return Optional.empty();

        var scopes = rows.stream()
                .filter(Objects::nonNull)
                .map(UserRow.Principal::toResourceScope)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());

        return Optional.of(
                UserPrincipal.of(
                        first.userId(),
                        first.displayName(),
                        first.createdAt(),
                        first.updatedAt(),
                        GrantedScope.of(
                                first.userId(),
                                scopes
                        )
                )
        );
    }

    @Override
    public UUID save(User user) {
        var sql = UserSql.INSERT;

        return jdbcClient.sql(sql)
                .param("displayName", user.getDisplayName())
                .param("createdAt", Timestamp.from(user.getCreatedAt()))
                .param("updatedAt", Timestamp.from(user.getUpdatedAt()))
                .query(UUID.class)
                .single();
    }
}
