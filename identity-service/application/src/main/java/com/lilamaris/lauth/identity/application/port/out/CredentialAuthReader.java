package com.lilamaris.lauth.identity.application.port.out;

import com.lilamaris.lauth.identity.application.model.credential.CredentialChallenge;

import java.util.Optional;

public interface CredentialAuthReader {
    Optional<CredentialChallenge> findByEmail(String email);
}
