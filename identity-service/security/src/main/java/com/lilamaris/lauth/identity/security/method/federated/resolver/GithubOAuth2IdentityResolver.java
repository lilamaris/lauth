package com.lilamaris.lauth.identity.security.method.federated.resolver;

import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GithubOAuth2IdentityResolver implements FederatedIdentityResolver {
    private final static String ID_KEY = "id";
    private final static String NICKNAME_KEY = "name";

    @Override
    public String registrationId() {
        return "github";
    }

    @Override
    public FederatedIdentity resolve(OAuth2User oAuth2User) {
        var attributes = oAuth2User.getAttributes();

        var providerUserNickname = getNickname(attributes);
        var providerUserId = getProviderUserId(attributes);
        return FederatedIdentity.of(registrationId(), providerUserId, providerUserNickname);
    }

    private String getProviderUserId(Map<String, Object> attributes) {
        var rawId = attributes.get(ID_KEY);
        if (rawId == null)
            throw new IllegalArgumentException("OAuth2 user id not found. registrationId=" + registrationId());

        var id = String.valueOf(rawId);
        if (id.isBlank())
            throw new IllegalArgumentException("Invalid OAuth2 user id. registrationId=" + registrationId() + " id=" + id);

        return id;
    }

    private @Nullable String getNickname(Map<String, Object> attributes) {
        var rawNickname = attributes.get(NICKNAME_KEY);
        if (rawNickname == null) return null;

        var nickname = String.valueOf(rawNickname);
        if (nickname.isBlank()) return null;

        return nickname;
    }
}
