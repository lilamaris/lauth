package com.lilamaris.lauth.identity.jdbc.utils;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.out.FederatedAccountStore;
import com.lilamaris.lauth.identity.application.port.out.FederatedUserPrincipalReader;
import com.lilamaris.lauth.identity.domain.FederatedAccount;
import com.lilamaris.lauth.identity.jdbc.row.UserRow;
import com.lilamaris.lauth.identity.jdbc.sql.FederatedAccountSql;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FederatedAccountJdbcAdapter implements FederatedAccountStore, FederatedUserPrincipalReader {
    private final JdbcClient jdbcClient;

    @Override
    public boolean tryCreate(FederatedAccount federatedAccount) {
        var sql = FederatedAccountSql.INSERT;

        return jdbcClient.sql(sql)
                .param("userId", federatedAccount.getUserId())
                .param("registrationId", federatedAccount.getRegistrationId())
                .param("providerUserId", federatedAccount.getProviderUserId())
                .param("createdAt", Timestamp.from(federatedAccount.getCreatedAt()))
                .update() == 1;
    }

    @Override
    public Optional<UserPrincipal> findByFederatedIdentity(String registrationId, String providerUserId) {
        var sql = FederatedAccountSql.FIND_USER_PRINCIPAL_BY_REGISTRATION_ID;

        return jdbcClient.sql(sql)
                .param("registrationId", registrationId)
                .param("providerUserId", providerUserId)
                .query(UserRow.Principal.class)
                .optional()
                .map(UserRow.Principal::toModel);
    }
}
