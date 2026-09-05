package com.lilamaris.lauth.identity.jpa;

import com.lilamaris.lauth.identity.application.port.out.ScopeReader;
import com.lilamaris.lauth.identity.domain.scope.Scope;
import com.lilamaris.lauth.identity.jpa.repository.ScopeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScopeJpaAdapter implements ScopeReader {
    private final ScopeRepository repository;

    @Override
    public Set<Scope> findAll() {
        return repository.findAll().stream().collect(Collectors.toUnmodifiableSet());
    }
}
