package com.lilamaris.lauth.identity.application.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;

@Validated
@ConfigurationProperties(prefix = "lauth.policy")
public record PolicyProperties(
        @Valid
        User user
) {
    public record User(
            @DefaultValue("/run/lauth/data/adjectives")
            @NotNull
            Path displayNameAdjectiveSource,

            @DefaultValue("/run/lauth/data/nouns")
            @NotNull
            Path displayNameNounSource
    ) {
    }
}
