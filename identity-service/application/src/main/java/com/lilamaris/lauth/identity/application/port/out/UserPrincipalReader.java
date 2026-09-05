package com.lilamaris.lauth.identity.application.port.out;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;

import java.util.Optional;
import java.util.UUID;

public interface UserPrincipalReader {
    Optional<UserPrincipal> findPrincipalById(UUID userId);
}
