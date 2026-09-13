package com.lilamaris.lauth.identity.application.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties(prefix = "lauth.auth.credential")
public record CredentialProperties(
        @DefaultValue("PT30M")
        @NotNull
        Duration passwordResetTokenExpiration
) {
}
