package com.lilamaris.lauth.identity.application.config.session;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties(prefix = "lauth.session.refresh-token")
public record RefreshTokenProperties(
        @DefaultValue("P7D")
        @NotNull
        Duration expiration
) {
}
