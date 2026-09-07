package com.lilamaris.lauth.identity.application.model.jwks;

import com.lilamaris.cozyr.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.cozyr.kernel.core.condition.StringPrecondition;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;

import java.time.Instant;

public record JWKMetadata(
        KeyRotationStatus status,
        String kid,
        JWSAlgorithm alg,
        Instant createdAt,
        JWK jwk
) {
    public JWKMetadata {
        ObjectPrecondition.requireNonNull(status, "status");
        StringPrecondition.requireNonBlank(kid, "kid");
        ObjectPrecondition.requireNonNull(alg, "alg");
        ObjectPrecondition.requireNonNull(createdAt, "createdAt");
        ObjectPrecondition.requireNonNull(jwk, "jwk");
    }

    public static JWKMetadata of(KeyRotationStatus status, String kid, JWSAlgorithm alg, Instant createdAt, JWK jwk) {
        return new JWKMetadata(status, kid, alg, createdAt, jwk);
    }
}
