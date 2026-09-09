package com.lilamaris.lauth.identity.application.port.out;

import com.lilamaris.lauth.identity.domain.RefreshToken;

import java.util.UUID;

public interface RefreshTokenStore {
    UUID save(RefreshToken refreshToken);
}
