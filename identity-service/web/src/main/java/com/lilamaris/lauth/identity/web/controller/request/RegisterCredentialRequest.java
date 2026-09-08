package com.lilamaris.lauth.identity.web.controller.request;

import com.lilamaris.lauth.identity.application.port.in.command.RegisterCredentialCommand;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.Nullable;

public record RegisterCredentialRequest(
        @Nullable String displayName,
        @NotBlank String email,
        @NotBlank String password
) {
    public RegisterCredentialCommand toCommand() {
        return RegisterCredentialCommand.of(displayName, email, password);
    }
}
