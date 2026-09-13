package com.lilamaris.lauth.identity.security.method.federated.resolver;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

public class OAuth2FederatedUserPrincipal extends DefaultOAuth2User implements FederatedUserPrincipal {
    private final UserPrincipal userPrincipal;

    public OAuth2FederatedUserPrincipal(@Nullable Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey, UserPrincipal userPrincipal) {
        super(authorities, attributes, nameAttributeKey);
        this.userPrincipal = ObjectPrecondition.requireNonNull(userPrincipal, "userPrincipal");
    }

    @Override
    public UserPrincipal user() {
        return userPrincipal;
    }
}
