package com.lilamaris.lauth.identity.application.port.in.command;

import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;
import org.jspecify.annotations.Nullable;

public record RegisterCredentialCommand(
        @Nullable String displayName,
        String email,
        String password
) {
    public RegisterCredentialCommand {
        StringPrecondition.requireNonBlank(email, "email");
        StringPrecondition.requireNonBlank(password, "password");
    }

    public static RegisterCredentialCommand of(@Nullable String displayName, String email, String password) {
        return new RegisterCredentialCommand(displayName, email, password);
    }
}
