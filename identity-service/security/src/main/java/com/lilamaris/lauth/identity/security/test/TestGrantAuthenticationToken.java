package com.lilamaris.lauth.identity.security.test;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.util.Map;
import java.util.UUID;

public class TestGrantAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {
    private final UUID userId;

    public TestGrantAuthenticationToken(UUID userId, Authentication clientPrincipal) {
        super(TestGrantType.TEST_GRANT_TYPE, clientPrincipal, Map.of());
        this.userId = userId;
    }

    public UUID userId() {
        return userId;
    }
}
