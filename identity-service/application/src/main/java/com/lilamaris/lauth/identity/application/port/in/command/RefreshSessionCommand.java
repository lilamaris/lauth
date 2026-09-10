package com.lilamaris.lauth.identity.application.port.in.command;

import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;

public record RefreshSessionCommand(
        String token
) {
    public RefreshSessionCommand {
        StringPrecondition.requireNonBlank(token, "token");
    }

    public static RefreshSessionCommand of(String token) {
        return new RefreshSessionCommand(token);
    }
}
