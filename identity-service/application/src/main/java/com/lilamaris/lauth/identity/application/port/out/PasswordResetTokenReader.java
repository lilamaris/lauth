package com.lilamaris.lauth.identity.application.port.out;

import com.lilamaris.lauth.identity.domain.PasswordResetToken;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenReader {
    Optional<PasswordResetToken> findById(UUID id);
}
