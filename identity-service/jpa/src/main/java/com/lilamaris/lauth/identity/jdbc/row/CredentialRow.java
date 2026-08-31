package com.lilamaris.lauth.identity.jdbc.row;

import com.lilamaris.lauth.identity.application.model.credential.CredentialChallenge;

import java.util.UUID;

public class CredentialRow {
    public record Challenge(
            UUID userId,
            String email,
            String passwordHash
    ) {
        public CredentialChallenge toModel() {
            return CredentialChallenge.of(userId, email, passwordHash);
        }
    }
}
