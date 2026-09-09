package com.lilamaris.lauth.identity.application.config.session;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "lauth.session")
public record SessionProperties(
        @DefaultValue("P14D")
        @NotNull
        Duration expiration
) {
}
