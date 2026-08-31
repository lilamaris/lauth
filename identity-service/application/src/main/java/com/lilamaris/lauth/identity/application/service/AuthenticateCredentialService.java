package com.lilamaris.lauth.identity.application.service;

import com.lilamaris.lauth.identity.application.exception.IdentityServiceProgressCode;
import com.lilamaris.lauth.identity.application.model.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.in.AuthenticateCredentialUseCase;
import com.lilamaris.lauth.identity.application.port.in.command.AuthenticateCredentialCommand;
import com.lilamaris.lauth.identity.application.port.out.CredentialAuthReader;
import com.lilamaris.lauth.identity.application.port.out.CredentialReader;
import com.lilamaris.lauth.identity.application.port.out.UserReader;
import com.lilamaris.shrturl.kernel.application.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class AuthenticateCredentialService implements AuthenticateCredentialUseCase {
    private final UserReader userReader;
    private final CredentialAuthReader credentialAuthReader;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    @Override
    public UserPrincipal authenticate(AuthenticateCredentialCommand command) {
        var email = command.email();
        var challenge = credentialAuthReader.findByEmail(email)
                .orElseThrow(() -> new ApplicationException(IdentityServiceProgressCode.CREDENTIAL_NOT_FOUND));

        var password = command.password();
        var isSuccess = passwordEncoder.matches(password, challenge.passwordHash());
        if (!isSuccess) throw new ApplicationException(IdentityServiceProgressCode.AUTHENTICATION_FAILED);

        var user = userReader.findById(challenge.userId())
                .orElseThrow(() -> new ApplicationException(IdentityServiceProgressCode.USER_NOT_FOUND));

        return UserPrincipal.from(user);
    }
}
