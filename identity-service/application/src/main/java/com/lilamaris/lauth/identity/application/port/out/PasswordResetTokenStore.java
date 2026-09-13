package com.lilamaris.lauth.identity.application.port.out;

import com.lilamaris.lauth.identity.domain.PasswordResetToken;

import java.time.Instant;
import java.util.UUID;

public interface PasswordResetTokenStore {
    boolean save(PasswordResetToken passwordResetToken);

    boolean revokeOpenByCredentialId(UUID credentialId, Instant revokedAt);

    boolean consume(UUID passwordResetTokenId, Instant consumedAt);
}
