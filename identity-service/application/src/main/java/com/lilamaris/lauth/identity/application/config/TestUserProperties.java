package com.lilamaris.lauth.identity.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;

@ConfigurationProperties(prefix = "lauth.test")
public record TestUserProperties(
        @DefaultValue("false") boolean enabled,
        @DefaultValue("1000") int userCount,
        @DefaultValue("/tmp/lauth-test-user-ids.txt") Path idsFile,
        String clientSecret
) {
    public TestUserProperties {
        if (enabled && (userCount < 1 || userCount > 1_000_000)) {
            throw new IllegalArgumentException("lauth.test.user-count must be between 1 and 1000000");
        }
        if (enabled && (clientSecret == null || clientSecret.isBlank())) {
            throw new IllegalArgumentException("lauth.test.client-secret is required when test mode is enabled");
        }
    }

    public UUID userId(int index) {
        if (index < 1 || index > userCount) throw new IllegalArgumentException("Invalid test user index: " + index);
        return UUID.nameUUIDFromBytes(("lauth:k6:test-user:" + index).getBytes(StandardCharsets.UTF_8));
    }

}
