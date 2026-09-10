package com.lilamaris.lauth.identity.security.principal;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@NullMarked
public record CurrentUserPrincipal(
        UserPrincipal user,
        Map<String, Object> attributes
) implements OAuth2AuthenticatedPrincipal {
    @Override
    public String getName() {
        return user.userId().toString();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
