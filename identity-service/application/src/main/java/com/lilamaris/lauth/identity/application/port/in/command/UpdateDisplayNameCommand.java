package com.lilamaris.lauth.identity.application.port.in.command;

import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateDisplayNameCommand(
        @NotNull UUID userId,
        @NotBlank
        @Size(
                max = 100,
                message = "100자를 넘을 수 없습니다."
        )
        String displayName
) {
    public UpdateDisplayNameCommand {
        ObjectPrecondition.requireNonNull(userId, "userId");
        StringPrecondition.requireNonBlank(displayName, "displayName");
    }

    public static UpdateDisplayNameCommand of(UUID usersId, String displayName) {
        return new UpdateDisplayNameCommand(usersId, displayName);
    }
}
