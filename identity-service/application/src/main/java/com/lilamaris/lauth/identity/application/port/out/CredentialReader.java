package com.lilamaris.lauth.identity.application.port.out;

import com.lilamaris.lauth.identity.application.model.credential.CredentialResetContext;

import java.util.Optional;

public interface CredentialReader {
    boolean existsByEmail(String email);

    Optional<CredentialResetContext> findContextByEmail(String email);
}
