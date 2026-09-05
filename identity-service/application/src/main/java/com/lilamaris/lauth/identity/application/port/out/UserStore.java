package com.lilamaris.lauth.identity.application.port.out;

import com.lilamaris.lauth.identity.domain.User;

import java.time.Instant;
import java.util.UUID;

public interface UserStore {
    UUID save(User user);
}
