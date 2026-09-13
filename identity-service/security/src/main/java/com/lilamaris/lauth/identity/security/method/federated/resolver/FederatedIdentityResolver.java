package com.lilamaris.lauth.identity.security.method.federated.resolver;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface FederatedIdentityResolver {
    String registrationId();

    FederatedIdentity resolve(OAuth2User oAuth2User);
}
