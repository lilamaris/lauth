package com.lilamaris.lauth.identity.jdbc;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.out.UserMetadataStore;
import com.lilamaris.lauth.identity.application.port.out.UserPrincipalReader;
import com.lilamaris.lauth.identity.application.port.out.UserStore;
import com.lilamaris.lauth.identity.application.port.out.status.UpdateHandleStatus;
import com.lilamaris.lauth.identity.domain.User;
import com.lilamaris.lauth.identity.jdbc.row.UserRow;
import com.lilamaris.lauth.identity.jdbc.sql.UserSql;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserJdbcAdapter implements UserStore, UserPrincipalReader, UserMetadataStore {
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

    @Override
    public UpdateHandleStatus updateHandle(UUID userId, String handle, Instant updatedAt) {
        var sql = UserSql.UPDATE_HANDLE;
        try {
            var updated = jdbcClient.sql(sql)
                    .param("userId", userId)
                    .param("handle", handle)
                    .param("updatedAt", Timestamp.from(updatedAt))
                    .update() == 1;
            return updated ? UpdateHandleStatus.UPDATED : UpdateHandleStatus.USER_NOT_FOUND;
        } catch (DuplicateKeyException e) {
            return UpdateHandleStatus.HANDLE_ALREADY_IN_USE;
        }
    }
}
