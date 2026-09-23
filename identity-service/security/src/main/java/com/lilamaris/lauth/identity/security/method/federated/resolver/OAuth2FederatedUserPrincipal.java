package com.lilamaris.lauth.identity.security.method.federated.resolver;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.security.principal.SerializableUserPrincipal;
import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@NullMarked
public class OAuth2FederatedUserPrincipal extends DefaultOAuth2User implements FederatedUserPrincipal {
    private final SerializableUserPrincipal userPrincipal;

    public OAuth2FederatedUserPrincipal(@Nullable Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey, UserPrincipal userPrincipal) {
        this(authorities, attributes, nameAttributeKey, SerializableUserPrincipal.from(userPrincipal));
    }

    public OAuth2FederatedUserPrincipal(@Nullable Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey, SerializableUserPrincipal userPrincipal) {
        super(authorities, attributes, nameAttributeKey);
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
