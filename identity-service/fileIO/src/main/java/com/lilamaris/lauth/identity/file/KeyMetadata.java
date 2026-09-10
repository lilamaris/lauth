package com.lilamaris.lauth.identity.file;

import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;
import com.nimbusds.jose.JWSAlgorithm;

import java.time.Instant;

public record KeyMetadata(
        String kid,
        JWSAlgorithm alg,
        Instant createdAt
) {
    public KeyMetadata {
        StringPrecondition.requireNonBlank(kid, "kid");
        ObjectPrecondition.requireNonNull(alg, "alg");
        ObjectPrecondition.requireNonNull(createdAt, "createdAt");
    }

    public static KeyMetadata of(String kid, JWSAlgorithm alg, Instant createdAt) {
        return new KeyMetadata(kid, alg, createdAt);
    }
}
