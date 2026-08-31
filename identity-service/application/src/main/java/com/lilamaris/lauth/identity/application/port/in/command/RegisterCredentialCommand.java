package com.lilamaris.lauth.identity.application.port.in.command;

import com.lilamaris.cozyr.kernel.core.condition.StringPrecondition;

public record RegisterCredentialCommand(
        String displayName,
        String email,
        String password
) {
    public RegisterCredentialCommand {
        StringPrecondition.requireNonBlank(displayName, "displayName");
        StringPrecondition.requireNonBlank(email, "email");
        StringPrecondition.requireNonBlank(password, "password");
    }

    public static RegisterCredentialCommand of(String displayName, String email, String password) {
        return new RegisterCredentialCommand(displayName, email, password);
    }
}
