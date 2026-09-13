package com.lilamaris.lauth.identity.security.method.federated.resolver;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Collection;

public class OidcFederatedUserPrincipal extends DefaultOidcUser implements FederatedUserPrincipal {
    private final UserPrincipal userPrincipal;

    public OidcFederatedUserPrincipal(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken, OidcUserInfo userInfo, UserPrincipal userPrincipal) {
        super(authorities, idToken, userInfo);
        this.userPrincipal = ObjectPrecondition.requireNonNull(userPrincipal, "userPrincipal");
    }

    @Override
    public UserPrincipal user() {
        return userPrincipal;
    }
}
