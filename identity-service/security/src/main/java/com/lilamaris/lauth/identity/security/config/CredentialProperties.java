package com.lilamaris.lauth.identity.security.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "lauth.security.credential")
public record CredentialProperties(
        @DefaultValue("/api/v1/auth/sign-in")
        @NotBlank
        String signInEndpoint
) {
}
