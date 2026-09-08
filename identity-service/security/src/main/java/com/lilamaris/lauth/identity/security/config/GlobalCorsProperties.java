package com.lilamaris.lauth.identity.security.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@ConfigurationProperties(prefix = "lauth.security.cors")
public record GlobalCorsProperties(
        List<@NotBlank String> allowedOrigins,

        @DefaultValue({"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"})
        List<@NotBlank String> allowedMethods,

        @DefaultValue({"Authorization", "Content-Type", "Accept", "Origins", "X-CSRF_TOKEN"})
        List<@NotBlank String> allowedHeaders,

        @DefaultValue("false")
        boolean allowCredentials,

        List<@NotBlank String> exposedHeaders
) {
}
