package com.lilamaris.lauth.identity.security.method.federated.resolver;

import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GoogleOidcIdentityResolver implements FederatedIdentityResolver {
    private static final String NICKNAME_KEY = "name";

    @Override
    public String registrationId() {
        return "google";
    }

    @Override
    public FederatedIdentity resolve(OAuth2User oAuth2User) {
        if (!(oAuth2User instanceof OidcUser oidcUser))
            throw new IllegalArgumentException("resolver requires OidcUser. registrationId=" + registrationId());

        var claims = oidcUser.getClaims();
        var providerUserId = oidcUser.getSubject();
        var providerUserNickname = getNickname(claims);

        return FederatedIdentity.of(registrationId(), providerUserId, providerUserNickname);
    }

    private @Nullable String getNickname(Map<String, Object> claims) {
        var rawNickname = claims.get(NICKNAME_KEY);
        if (rawNickname == null) return null;

        var nickname = String.valueOf(rawNickname);
        if (nickname.isBlank()) return null;

        return nickname;
    }
}
