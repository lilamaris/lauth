package com.lilamaris.lauth.identity.security.method.federated.resolver;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.security.principal.SerializableUserPrincipal;
import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Collection;

@NullMarked
public class OidcFederatedUserPrincipal extends DefaultOidcUser implements FederatedUserPrincipal {
    private final SerializableUserPrincipal userPrincipal;

    public OidcFederatedUserPrincipal(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken, @Nullable OidcUserInfo userInfo, UserPrincipal userPrincipal) {
        this(authorities, idToken, userInfo, "sub", SerializableUserPrincipal.from(userPrincipal));
    }

    public OidcFederatedUserPrincipal(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken, @Nullable OidcUserInfo userInfo, String nameAttributeKey, SerializableUserPrincipal userPrincipal) {
        super(authorities, idToken, userInfo, nameAttributeKey);
        this.userPrincipal = ObjectPrecondition.requireNonNull(userPrincipal, "userPrincipal");
    }

    @Override
    public SerializableUserPrincipal user() {
        return userPrincipal;
    }

    @Override
    public String getName() {
        return userPrincipal.userId().toString();
    }
}
