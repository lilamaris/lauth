package com.lilamaris.lauth.identity.application.port.out;

import com.lilamaris.lauth.identity.domain.User;

import java.time.Instant;

public interface UserStore {
    User save(String displayName, Instant createdAt);
}
