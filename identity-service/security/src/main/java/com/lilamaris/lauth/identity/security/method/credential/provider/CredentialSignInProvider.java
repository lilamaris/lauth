package com.lilamaris.lauth.identity.security.method.credential.provider;

import com.lilamaris.lauth.identity.application.port.in.AuthenticateCredentialUseCase;
import com.lilamaris.lauth.identity.security.method.credential.request.CredentialAuthenticateToken;
import com.lilamaris.lauth.kernel.application.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@NullMarked
@RequiredArgsConstructor
public class CredentialSignInProvider implements AuthenticationProvider {
    private final AuthenticateCredentialUseCase authenticateCredentialUseCase;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var auth = (CredentialAuthenticateToken) authentication;
        var command = auth.toAuthenticateCommand();

        try {
            var principal = authenticateCredentialUseCase.authenticate(command);
            return CredentialAuthenticateToken.of(principal);
        } catch (ApplicationException e) {
            throw new AuthenticationServiceException("Credential Sign-in failed", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CredentialAuthenticateToken.class.isAssignableFrom(authentication);
    }
}
