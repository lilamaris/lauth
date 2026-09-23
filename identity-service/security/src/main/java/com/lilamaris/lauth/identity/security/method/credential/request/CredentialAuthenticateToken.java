package com.lilamaris.lauth.identity.security.method.credential.request;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.in.command.AuthenticateCredentialCommand;
import com.lilamaris.lauth.identity.security.method.credential.model.Credential;
import com.lilamaris.lauth.identity.security.principal.SerializableUserPrincipal;
import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;

@NullMarked
public class CredentialAuthenticateToken extends AbstractAuthenticationToken {
    @Nullable
    private SerializableUserPrincipal principal;
    @Nullable
    private Credential credential;

    private CredentialAuthenticateToken(Credential credential) {
        super(List.of());
        setAuthenticated(false);
        this.credential = ObjectPrecondition.requireNonNull(credential, "credential");
    }

    private CredentialAuthenticateToken(UserPrincipal principal) {
        this(SerializableUserPrincipal.from(principal));
    }

    public CredentialAuthenticateToken(SerializableUserPrincipal principal) {
        super(List.of());
        this.principal = ObjectPrecondition.requireNonNull(principal, "principal");
        setAuthenticated(true);
    }

    public static CredentialAuthenticateToken of(Credential credential) {
        return new CredentialAuthenticateToken(credential);
    }

    public static CredentialAuthenticateToken of(UserPrincipal principal) {
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
    public String getName() {
        if (principal == null) return "";
        return principal.userId().toString();
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
