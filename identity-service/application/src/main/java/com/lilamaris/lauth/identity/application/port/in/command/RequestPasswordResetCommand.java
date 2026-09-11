package com.lilamaris.lauth.identity.application.port.in.command;

import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequestPasswordResetCommand(
        @NotBlank @Email String email
) {
    public RequestPasswordResetCommand {
        StringPrecondition.requireNonBlank(email, "email");
    }

    public static RequestPasswordResetCommand of(String email) {
        return new RequestPasswordResetCommand(email);
    }
}
