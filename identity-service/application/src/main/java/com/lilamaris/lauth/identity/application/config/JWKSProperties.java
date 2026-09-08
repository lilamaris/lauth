package com.lilamaris.lauth.identity.application.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;

@Validated
@ConfigurationProperties(prefix = "lauth.jwks")
public record JWKSProperties(
        @DefaultValue("/run/lauth/secrets")
        @NotNull
        Path keySource,

        @NotBlank
        String activeKid
) {
}
