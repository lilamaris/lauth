package com.lilamaris.lauth.identity.application.exception;

import com.lilamaris.lauth.kernel.application.exception.ProgressType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum IdentityServiceProgressType implements ProgressType {
    AUTHENTICATION_FAILED("authentication-failed"),
    TOKEN_VERIFICATION_FAILED("token-verification-failed"),
    RESOURCE_EXPIRED("resource-expired"),
    GRANT_FAILED("grant-failed");

    private final String canonicalName;

    @Override
    public String canonicalName() {
        return canonicalName;
    }
}
