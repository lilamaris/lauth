package com.lilamaris.lauth.identity.web.controller.request;

import com.lilamaris.lauth.identity.application.port.in.command.RefreshSessionCommand;
import jakarta.validation.constraints.NotBlank;

public record RefreshSessionRequest(
        @NotBlank String token
) {
    public RefreshSessionCommand toCommand() {
        return RefreshSessionCommand.of(token);
    }
}
