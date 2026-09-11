package com.lilamaris.lauth.identity.application.port.in.result;

import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;

import java.util.UUID;

public record UpdateHandleResult(
        UUID userId,
        String handle
) {
    public UpdateHandleResult {
        ObjectPrecondition.requireNonNull(userId, "userId");
        StringPrecondition.requireNonBlank(handle, "handle");
    }

    public static UpdateHandleResult of(UUID userId, String handle) {
        return new UpdateHandleResult(userId, handle);
    }
}
