package com.lilamaris.lauth.identity.application.internal;

import com.lilamaris.lauth.identity.application.exception.IdentityServiceProgressCode;
import com.lilamaris.lauth.identity.application.model.scope.GrantedScope;
import com.lilamaris.lauth.identity.application.model.scope.ResourceScope;
import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.out.ScopeReader;
import com.lilamaris.lauth.identity.application.port.out.UserGrantStore;
import com.lilamaris.lauth.identity.application.port.out.UserStore;
import com.lilamaris.lauth.identity.domain.User;
import com.lilamaris.lauth.identity.domain.scope.Scope;
import com.lilamaris.shrturl.kernel.application.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserService {
    private final UserStore userStore;
    private final UserGrantStore userGrantStore;
    private final ScopeReader scopeReader;

    public UserPrincipal createNewUser(String displayName, Instant createdAt) {
        var user = User.of(displayName, createdAt);
        var userId = userStore.save(user);

        var scopes = scopeReader.findAll();
        var scopeIds = scopes.stream().map(Scope::getId).collect(Collectors.toUnmodifiableSet());
        var resourceScopes = scopes.stream().map(ResourceScope::from).collect(Collectors.toUnmodifiableSet());

        if (userGrantStore.grantAll(userId, scopeIds, createdAt))
            throw new ApplicationException(IdentityServiceProgressCode.SCOPE_GRANT_FAILED);

        return UserPrincipal.of(
                userId,
                user.getDisplayName(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                GrantedScope.of(userId, resourceScopes)
        );
    }
}