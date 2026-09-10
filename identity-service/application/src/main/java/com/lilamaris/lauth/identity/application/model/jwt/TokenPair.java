package com.lilamaris.lauth.identity.application.model.jwt;

import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;

public record TokenPair(
        TokenMetadata accessToken,
        TokenMetadata refreshToken
) {
    public TokenPair {
        ObjectPrecondition.requireNonNull(accessToken, "accessToken");
        ObjectPrecondition.requireNonNull(refreshToken, "refreshToken");
    }

    public static TokenPair of(TokenMetadata accessToken, TokenMetadata refreshToken) {
        return new TokenPair(accessToken, refreshToken);
    }
}
