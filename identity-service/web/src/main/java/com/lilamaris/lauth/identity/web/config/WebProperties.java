package com.lilamaris.lauth.identity.web.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@Validated
@ConfigurationProperties(prefix = "lauth.web")
public record WebProperties(
        @DefaultValue("https://lauth.lilamaris.kr")
        @NotNull
        URI baseUrl
) {
}
