package com.lilamaris.lauth.identity.application.internal.id;

import com.github.f4b6a3.uuid.UuidCreator;

import java.util.UUID;

public class UUIDv7Generator implements IdGenerator<UUID> {
    @Override
    public UUID generate() {
        return UuidCreator.getTimeOrderedEpoch();
    }
}
