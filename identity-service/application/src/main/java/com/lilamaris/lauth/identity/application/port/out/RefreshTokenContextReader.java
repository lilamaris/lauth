package com.lilamaris.lauth.identity.application.port.out;

import com.lilamaris.lauth.identity.application.model.session.RefreshTokenContext;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenContextReader {
    Optional<RefreshTokenContext> findByRefreshTokenId(UUID refreshTokenId);
}
