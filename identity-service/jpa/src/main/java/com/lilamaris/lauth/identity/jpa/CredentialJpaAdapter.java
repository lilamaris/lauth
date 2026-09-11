package com.lilamaris.lauth.identity.jpa;

import com.lilamaris.lauth.identity.application.model.credential.CredentialResetContext;
import com.lilamaris.lauth.identity.application.port.out.CredentialReader;
import com.lilamaris.lauth.identity.jpa.repository.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CredentialJpaAdapter implements CredentialReader {
    private final CredentialRepository repository;

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Optional<CredentialResetContext> findContextByEmail(String email) {
        return repository.findContextByEmail(email);
    }
}
