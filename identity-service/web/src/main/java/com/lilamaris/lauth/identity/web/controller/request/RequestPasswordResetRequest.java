package com.lilamaris.lauth.identity.web.controller.request;

import com.lilamaris.lauth.identity.application.port.in.command.RequestPasswordResetCommand;
import jakarta.validation.constraints.NotBlank;

public record RequestPasswordResetRequest(
        @NotBlank String email
) {
    public RequestPasswordResetCommand toCommand() {
        return RequestPasswordResetCommand.of(email);
    }
}
