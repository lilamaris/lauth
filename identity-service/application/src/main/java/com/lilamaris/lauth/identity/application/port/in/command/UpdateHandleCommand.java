package com.lilamaris.lauth.identity.application.port.in.command;

import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
public record UpdateHandleCommand(
        @NotNull UUID userId,
        @NotBlank
        @Pattern(regexp = "^[a-z][a-z0-9_]{2,29}$")
        String handle
) {
    public UpdateHandleCommand {
        ObjectPrecondition.requireNonNull(userId, "userId");
        StringPrecondition.requireNonBlank(handle, "handle");
    }

    public static UpdateHandleCommand of(UUID userId, String handle) {
        return new UpdateHandleCommand(userId, handle);
    }
}
