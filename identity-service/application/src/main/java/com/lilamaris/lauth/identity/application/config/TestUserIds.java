package com.lilamaris.lauth.identity.application.config;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

public final class TestUserIds {
    private final List<UUID> ids;
    private final Set<UUID> lookup;

    public TestUserIds(TestUserProperties properties) {
        this.ids = IntStream.rangeClosed(1, properties.userCount())
                .mapToObj(properties::userId)
                .toList();
        this.lookup = Set.copyOf(ids);
    }

    public List<UUID> ids() {
        return ids;
    }

    public boolean contains(UUID userId) {
        return lookup.contains(userId);
    }
}
