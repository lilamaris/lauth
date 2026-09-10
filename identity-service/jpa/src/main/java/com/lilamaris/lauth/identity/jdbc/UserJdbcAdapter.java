package com.lilamaris.lauth.identity.jdbc;

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
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserJdbcAdapter implements UserStore, UserPrincipalReader {
    private final JdbcClient jdbcClient;

    @Override
    public Optional<UserPrincipal> findPrincipalById(UUID userId) {
        var sql = UserSql.FIND_PRINCIPAL_BY_ID;

        return jdbcClient.sql(sql)
                .param("userId", userId)
                .query(UserRow.Principal.class)
                .optional()
                .map(UserRow.Principal::toModel);
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
