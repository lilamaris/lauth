package com.lilamaris.lauth.identity.application.port.in.command;

import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordCommand(
        @NotBlank String token,
        @NotBlank String password
) {
    public ResetPasswordCommand {
        StringPrecondition.requireNonBlank(token, "token");
        StringPrecondition.requireNonBlank(password, "password");
    }

    public static ResetPasswordCommand of(String token, String password) {
        return new ResetPasswordCommand(token, password);
    }
}
