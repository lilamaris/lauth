package com.lilamaris.lauth.identity.application.service;

import com.lilamaris.lauth.identity.application.exception.IdentityServiceProgressCode;
import com.lilamaris.lauth.identity.application.model.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.in.RegisterCredentialUseCase;
import com.lilamaris.lauth.identity.application.port.in.command.RegisterCredentialCommand;
import com.lilamaris.lauth.identity.application.port.out.CredentialReader;
import com.lilamaris.lauth.identity.application.port.out.CredentialStore;
import com.lilamaris.lauth.identity.application.port.out.UserStore;
import com.lilamaris.shrturl.kernel.application.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class RegisterCredentialService implements RegisterCredentialUseCase {
    private final CredentialReader credentialReader;
    private final CredentialStore credentialStore;
    private final UserStore userStore;

    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    @Override
    @Transactional
    public UserPrincipal register(RegisterCredentialCommand command) {
        var email = command.email();
        var isExistsEmail = credentialReader.existsByEmail(email);
        if (isExistsEmail) throw new ApplicationException(IdentityServiceProgressCode.EMAIL_DUPLICATED);

        var now = clock.instant();
        var displayName = command.displayName();
        var passwordHash = passwordEncoder.encode(command.password());
        var savedUser = userStore.save(displayName, now);
        var isCredentialCreated = credentialStore.save(savedUser.getId(), email, passwordHash, now);
        if (!isCredentialCreated) throw new ApplicationException(IdentityServiceProgressCode.EMAIL_DUPLICATED);

        return UserPrincipal.from(savedUser);
    }
}
