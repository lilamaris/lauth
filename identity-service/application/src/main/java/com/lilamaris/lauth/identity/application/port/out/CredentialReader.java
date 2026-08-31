package com.lilamaris.lauth.identity.application.port.out;

public interface CredentialReader {
    boolean existsByEmail(String email);
}
