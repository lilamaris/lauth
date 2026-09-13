package com.lilamaris.lauth.identity.application.model.credential;

import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;

import java.util.UUID;

public record CredentialResetContext(
        UUID userId,
        UUID credentialId
) {
    public CredentialResetContext {
        ObjectPrecondition.requireNonNull(userId, "userId");
        ObjectPrecondition.requireNonNull(credentialId, "credentialId");
    }

    public static CredentialResetContext of(UUID userId, UUID credentialId) {
        return new CredentialResetContext(userId, credentialId);
    }
}
