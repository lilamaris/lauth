package com.lilamaris.lauth.identity.security.method.credential.request;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.in.command.AuthenticateCredentialCommand;
import com.lilamaris.lauth.identity.security.method.credential.model.Credential;
import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;

public class CredentialAuthenticateToken extends AbstractAuthenticationToken {
    @Nullable
    private UserPrincipal principal;
    @Nullable
    private Credential credential;

    private CredentialAuthenticateToken(@NonNull Credential credential) {
        super(List.of());
        setAuthenticated(false);
        this.credential = ObjectPrecondition.requireNonNull(credential, "credential");
    }

    private CredentialAuthenticateToken(@NonNull UserPrincipal principal) {
        super(List.of());
        setAuthenticated(true);
        this.principal = principal;
    }

    public static CredentialAuthenticateToken of(@NonNull Credential credential) {
        return new CredentialAuthenticateToken(credential);
    }

    public static CredentialAuthenticateToken of(@NonNull UserPrincipal principal) {
        return new CredentialAuthenticateToken(principal);
    }

    public AuthenticateCredentialCommand toAuthenticateCommand() {
        if (credential == null)
            throw new IllegalArgumentException("Can not create authenticate command. Because credential is null.");
        return AuthenticateCredentialCommand.of(credential.email(), credential.password());
    }

    @Override
    public @Nullable Object getCredentials() {
        return credential;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credential = null;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return principal;
    }
}
