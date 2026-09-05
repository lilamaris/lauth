package com.lilamaris.lauth.identity.application.config;

import com.lilamaris.lauth.identity.domain.scope.Action;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "lauth.scopes")
public record ScopeProperties(
        @Valid
        @DefaultValue("false")
        boolean sync,

        @NotNull
        List<@Valid @NotNull ScopeDefinition> definitions
) {
    public record ScopeDefinition(
            @NotBlank
            String resource,

            @NotNull
            Action action,

            @NotBlank
            String description
    ) {
    }
}
