package com.lilamaris.lauth.identity.web.controller.request;

import com.lilamaris.lauth.identity.application.port.in.command.UpdateHandleCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateHandleRequest(
        @Size(max = 30) @NotBlank String handle
) {
    public UpdateHandleCommand toCommand(UUID userId) {
        return UpdateHandleCommand.of(userId, handle);
    }
}
