package com.lilamaris.lauth.identity.application.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "lauth")
public record ClientRegistrationProperties(
        Map<String, @Valid @NotNull RegisteredClient> clients
) {
    public record RegisteredClient(
            String displayName,
            URI passwordResetUri
    ) {
    }
}
