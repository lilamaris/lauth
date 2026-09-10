package com.lilamaris.lauth.identity.application.port.in.command;

import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;

public record AuthenticateCredentialCommand(
        String email,
        String password
) {
    public AuthenticateCredentialCommand {
        StringPrecondition.requireNonBlank(email, "email");
        StringPrecondition.requireNonBlank(password, "password");
    }

    public static AuthenticateCredentialCommand of(String email, String password) {
        return new AuthenticateCredentialCommand(email, password);
    }
}
