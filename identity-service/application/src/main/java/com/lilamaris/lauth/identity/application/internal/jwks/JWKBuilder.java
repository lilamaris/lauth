package com.lilamaris.lauth.identity.application.internal.jwks;

import com.lilamaris.lauth.identity.application.model.jwks.JWKMetadata;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;

public interface JWKBuilder<T extends JWK> {
    T build(JWKMetadata metadata) throws Exception;

    Class<T> target();

    JWSAlgorithm support();
}
