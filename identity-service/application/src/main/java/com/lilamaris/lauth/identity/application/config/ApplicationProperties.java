package com.lilamaris.lauth.identity.application.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.ZoneId;

@Validated
@ConfigurationProperties(prefix = "lauth")
public record ApplicationProperties(
        @DefaultValue("UTC")
        @NotNull
        ZoneId timezone,

        @NotBlank
        String hasherKey
) {
}
