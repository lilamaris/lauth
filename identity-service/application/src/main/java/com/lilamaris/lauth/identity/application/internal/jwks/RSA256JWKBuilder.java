package com.lilamaris.lauth.identity.application.internal.jwks;

import com.lilamaris.lauth.identity.application.model.jwks.JWKMetadata;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
public class RSA256JWKBuilder implements JWKBuilder<RSAKey> {
    public static final JWSAlgorithm alg = JWSAlgorithm.RS256;

    @Override
    public RSAKey build(JWKMetadata metadata) {
        if (!(metadata.jwk() instanceof RSAKey jwk))
            throw new IllegalArgumentException("JWK type does not match with RSAKey. kid=" + metadata.kid());

        return new RSAKey.Builder(jwk)
                .keyID(metadata.kid())
                .algorithm(metadata.alg())
                .keyUse(KeyUse.SIGNATURE)
                .issueTime(Date.from(metadata.createdAt()))
                .build();
    }

    @Override
    public Class<RSAKey> target() {
        return RSAKey.class;
    }

    @Override
    public JWSAlgorithm support() {
        return alg;
    }
}
