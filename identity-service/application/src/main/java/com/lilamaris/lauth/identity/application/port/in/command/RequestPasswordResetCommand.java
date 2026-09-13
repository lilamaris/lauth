package com.lilamaris.lauth.identity.application.port.in.command;

import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequestPasswordResetCommand(
        @NotBlank @Email String email,
        @NotBlank String clientId
) {
    public RequestPasswordResetCommand {
        StringPrecondition.requireNonBlank(email, "email");
        StringPrecondition.requireNonBlank(clientId, "clientId");
    }

    public static RequestPasswordResetCommand of(String email, String clientId) {
        return new RequestPasswordResetCommand(email, clientId);
    }
}
