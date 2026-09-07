package com.lilamaris.lauth.identity.application.port.in;

import com.nimbusds.jose.jwk.JWK;

import java.util.List;

public interface ListVerifiableJWKSUseCase {
    List<JWK> list();
}
