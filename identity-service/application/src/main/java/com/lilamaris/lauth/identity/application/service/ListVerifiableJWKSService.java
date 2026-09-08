package com.lilamaris.lauth.identity.application.service;

import com.lilamaris.lauth.identity.application.internal.jwks.JWKSRegistry;
import com.lilamaris.lauth.identity.application.port.in.ListVerifiableJWKSUseCase;
import com.nimbusds.jose.jwk.JWK;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListVerifiableJWKSService implements ListVerifiableJWKSUseCase {
    private final JWKSRegistry registry;

    @Override
    public List<JWK> list() {
        return registry.getAll().stream()
                .map(JWK::toPublicJWK)
                .toList();
    }
}
