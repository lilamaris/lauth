package com.lilamaris.lauth.identity.application.port.out;

import com.lilamaris.lauth.identity.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserReader {
    Optional<User> findById(UUID userId);
}
