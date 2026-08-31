package com.lilamaris.lauth.identity.jpa;

import com.lilamaris.lauth.identity.application.port.out.UserReader;
import com.lilamaris.lauth.identity.application.port.out.UserStore;
import com.lilamaris.lauth.identity.domain.User;
import com.lilamaris.lauth.identity.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserJpaAdapter implements UserStore, UserReader {
    private final UserRepository repository;

    @Override
    public User save(String displayName, Instant createdAt) {
        var user = User.of(displayName, createdAt);

        return repository.save(user);
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return repository.findById(userId);
    }
}
