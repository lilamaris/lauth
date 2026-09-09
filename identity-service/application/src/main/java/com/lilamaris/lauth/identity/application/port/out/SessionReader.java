package com.lilamaris.lauth.identity.application.port.out;

import java.util.UUID;

public interface SessionReader {
    boolean existsById(UUID sessionId);
}
