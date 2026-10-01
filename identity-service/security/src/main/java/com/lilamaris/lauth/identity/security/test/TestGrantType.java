package com.lilamaris.lauth.identity.security.test;

import org.springframework.security.oauth2.core.AuthorizationGrantType;

public final class TestGrantType {
    public static final AuthorizationGrantType TEST_GRANT_TYPE = new AuthorizationGrantType("urn:lauth:grant-type:test");
}
