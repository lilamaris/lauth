package com.lilamaris.lauth.identity.application.service;

import com.lilamaris.lauth.identity.application.exception.IdentityServiceProgressCode;
import com.lilamaris.lauth.identity.application.internal.account.FederatedTxService;
import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.in.ResolveFederatedAccountUseCase;
import com.lilamaris.lauth.identity.application.port.in.command.ResolveFederatedAccountCommand;
import com.lilamaris.lauth.identity.application.port.out.FederatedUserPrincipalReader;
import com.lilamaris.lauth.kernel.application.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResolveFederatedAccountService implements ResolveFederatedAccountUseCase {
    private final FederatedUserPrincipalReader federatedUserPrincipalReader;
    private final FederatedTxService federatedTxService;

    @Override
    public UserPrincipal resolve(ResolveFederatedAccountCommand command) {
        var registrationId = command.registrationId();
        var providerUserId = command.providerUserId();
        var existsUser = federatedUserPrincipalReader.findByFederatedIdentity(registrationId, providerUserId);
        if (existsUser.isPresent()) return existsUser.get();

        try {
            return federatedTxService.register(command);
        } catch (ApplicationException e) {
            if (e.getApplicationCode().equals(IdentityServiceProgressCode.FEDERATED_ACCOUNT_ALREADY_EXISTS)) {
                return federatedUserPrincipalReader.findByFederatedIdentity(registrationId, providerUserId)
                        .orElseThrow(() -> new ApplicationException(IdentityServiceProgressCode.FEDERATED_ACCOUNT_REGISTRATION_FAILED));
            }
            throw e;
        }
    }
}
