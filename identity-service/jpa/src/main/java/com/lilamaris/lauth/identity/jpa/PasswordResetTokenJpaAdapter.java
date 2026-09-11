package com.lilamaris.lauth.identity.jpa;

import com.lilamaris.lauth.identity.application.port.out.PasswordResetTokenReader;
import com.lilamaris.lauth.identity.domain.PasswordResetToken;
import com.lilamaris.lauth.identity.jpa.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PasswordResetTokenJpaAdapter implements PasswordResetTokenReader {
    private final PasswordResetTokenRepository repository;

    @Override
    public Optional<PasswordResetToken> findById(UUID id) {
        return repository.findById(id);
    }
}
