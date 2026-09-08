package com.lilamaris.lauth.identity.application.model.jwt;

import com.lilamaris.cozyr.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.cozyr.kernel.core.condition.StringPrecondition;
import com.lilamaris.cozyr.kernel.core.condition.TimePrecondition;

import java.time.Instant;

public record TokenMetadata(
        TokenType type,
        String value,
        Instant issuedAt,
        Instant expiresAt
) {
    public TokenMetadata {
        ObjectPrecondition.requireNonNull(type, "type");
        StringPrecondition.requireNonBlank(value, "value");
        ObjectPrecondition.requireNonNull(issuedAt, "issuedAt");
        TimePrecondition.requireAfterOrEqual(issuedAt, expiresAt, "issuedAt", "expiresAt");
    }

    public static TokenMetadata accessToken(String value, Instant issuedAt, Instant expiresAt) {
        return new TokenMetadata(TokenType.ACCESS, value, issuedAt, expiresAt);
    }

    public static TokenMetadata refreshToken(String value, Instant issuedAt, Instant expiresAt) {
        return new TokenMetadata(TokenType.REFRESH, value, issuedAt, expiresAt);
    }

    public static TokenMetadata of(TokenType type, String value, Instant issuedAt, Instant expiresAt) {
        return new TokenMetadata(type, value, issuedAt, expiresAt);
    }
}
