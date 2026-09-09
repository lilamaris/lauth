package com.lilamaris.lauth.identity.application.port.out;

import com.lilamaris.lauth.identity.domain.Session;

import java.util.UUID;

public interface SessionStore {
    UUID save(Session session);
}
