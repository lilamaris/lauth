package com.lilamaris.lauth.identity.application.internal.account;

import com.lilamaris.lauth.identity.application.exception.IdentityServiceProgressCode;
import com.lilamaris.lauth.identity.application.internal.UserService;
import com.lilamaris.lauth.identity.application.internal.random.RandomDisplayName;
import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.in.command.ResolveFederatedAccountCommand;
import com.lilamaris.lauth.identity.application.port.out.FederatedAccountStore;
import com.lilamaris.lauth.identity.domain.FederatedAccount;
import com.lilamaris.lauth.kernel.application.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FederatedTxService {
    private final FederatedAccountStore federatedAccountStore;
    private final UserService userService;
    private final RandomDisplayName randomDisplayName;
    private final Clock clock;

    @Transactional
    public UserPrincipal register(ResolveFederatedAccountCommand command) {
        var registrationId = command.registrationId();
        var providerUserId = command.providerUserId();

        var now = clock.instant();
        var displayName = Optional.ofNullable(command.providerUserNickname())
                .orElseGet(randomDisplayName::generate);
        var userPrincipal = userService.createNewUser(displayName, now);

        var federatedAccount = FederatedAccount.of(userPrincipal.userId(), registrationId, providerUserId, now);
        var created = federatedAccountStore.tryCreate(federatedAccount);
        if (!created) {
            throw new ApplicationException(IdentityServiceProgressCode.FEDERATED_ACCOUNT_ALREADY_EXISTS);
        }
        return userPrincipal;
    }
}
