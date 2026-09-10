package com.lilamaris.lauth.identity.application.service;

import com.lilamaris.lauth.identity.application.exception.IdentityServiceProgressCode;
import com.lilamaris.lauth.identity.application.internal.UserService;
import com.lilamaris.lauth.identity.application.internal.random.RandomDisplayName;
import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.in.RegisterCredentialUseCase;
import com.lilamaris.lauth.identity.application.port.in.command.RegisterCredentialCommand;
import com.lilamaris.lauth.identity.application.port.out.CredentialReader;
import com.lilamaris.lauth.identity.application.port.out.CredentialStore;
import com.lilamaris.lauth.kernel.application.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegisterCredentialService implements RegisterCredentialUseCase {
    private final CredentialReader credentialReader;
    private final CredentialStore credentialStore;
    private final UserService userService;

    private final RandomDisplayName randomDisplayName;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    @Override
    @Transactional
    public UserPrincipal register(RegisterCredentialCommand command) {
        var email = command.email();
        var isExistsEmail = credentialReader.existsByEmail(email);
        if (isExistsEmail) throw new ApplicationException(IdentityServiceProgressCode.EMAIL_DUPLICATED);

        var now = clock.instant();
        var displayName = Optional.ofNullable(command.displayName())
                .orElseGet(randomDisplayName::generate);
        var userPrincipal = userService.createNewUser(displayName, now);

        var passwordHash = passwordEncoder.encode(command.password());
        var isCredentialCreated = credentialStore.save(userPrincipal.userId(), email, passwordHash, now);
        if (!isCredentialCreated) throw new ApplicationException(IdentityServiceProgressCode.EMAIL_DUPLICATED);

        return userPrincipal;
    }
}
