package com.lilamaris.lauth.identity.application.internal.jwks;

import com.lilamaris.lauth.identity.application.model.jwks.JWKMetadata;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetKeyPair;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Ed25519JWKBuilder implements JWKBuilder<OctetKeyPair> {
    public static final JWSAlgorithm alg = JWSAlgorithm.Ed25519;

    @Override
    public OctetKeyPair build(JWKMetadata metadata) {
        if (!(metadata.jwk() instanceof OctetKeyPair ed25519Key))
            throw new IllegalArgumentException("JWK type does not match with Ed25519. kid=" + metadata.kid());

        return new OctetKeyPair.Builder(ed25519Key)
                .keyID(metadata.kid())
                .algorithm(metadata.alg())
                .keyUse(KeyUse.SIGNATURE)
                .issueTime(Date.from(metadata.createdAt()))
                .build();
    }

    @Override
    public Class<OctetKeyPair> target() {
        return OctetKeyPair.class;
    }

    @Override
    public JWSAlgorithm support() {
        return alg;
    }
}
