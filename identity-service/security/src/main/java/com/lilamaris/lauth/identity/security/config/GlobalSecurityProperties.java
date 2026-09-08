package com.lilamaris.lauth.identity.security.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "lauth.security")
public record GlobalSecurityProperties(
        @DefaultValue("false")
        boolean csrfEnabled,

        List<@NotBlank String> permits
) {
}
