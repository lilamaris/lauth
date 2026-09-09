package com.lilamaris.lauth.identity.application.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "lauth.jwt")
public record JwtProperties(
        @Valid
        @DefaultValue
        AccessToken accessToken,

        @Valid
        @DefaultValue
        RefreshToken refreshToken
) {
    public record AccessToken(
            @DefaultValue("lauth")
            @NotBlank
            String issuer,

            @DefaultValue("PT15M")
            @NotNull
            Duration expiration
    ) {
    }

    public record RefreshToken(
            @DefaultValue("P7D")
            @NotNull
            Duration expiration
    ) {
    }
}
