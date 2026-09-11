package com.lilamaris.lauth.identity.web.controller.request;

import com.lilamaris.lauth.identity.application.port.in.command.ResetPasswordCommand;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank String token,
        @NotBlank String password
) {
    public ResetPasswordCommand toCommand() {
        return ResetPasswordCommand.of(token, password);
    }
}
