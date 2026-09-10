package com.lilamaris.lauth.identity.application.model.credential;

import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;

import java.util.UUID;

public record CredentialChallenge(
        UUID userId,
        String email,
        String passwordHash
) {
    public CredentialChallenge {
        ObjectPrecondition.requireNonNull(userId, "userId");
        StringPrecondition.requireNonBlank(email, "email");
        StringPrecondition.requireNonBlank(passwordHash, "passwordHash");
    }

    public static CredentialChallenge of(UUID userId, String email, String passwordHash) {
        return new CredentialChallenge(userId, email, passwordHash);
    }
}
