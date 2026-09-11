package com.lilamaris.lauth.identity.web.controller.request;

import com.lilamaris.lauth.identity.application.port.in.command.UpdateDisplayNameCommand;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdateDisplayNameRequest(
        @NotBlank
        String displayName
) {
    public UpdateDisplayNameCommand toCommand(UUID userId) {
        return UpdateDisplayNameCommand.of(userId, displayName);
    }
}
