package com.lilamaris.lauth.identity.application.port.in.command;

import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record UpdateHandleCommand(
        @NotNull UUID userId,
        @NotBlank
        @Pattern(
                regexp = "^[a-z][a-z0-9_]{2,29}$",
                message = "핸들은 영문 소문자로 시작하고 소문자, 숫자, 밑줄로 구성된 3~30자여야 합니다."
        )
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
