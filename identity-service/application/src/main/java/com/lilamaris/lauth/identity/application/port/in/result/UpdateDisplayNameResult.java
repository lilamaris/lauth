package com.lilamaris.lauth.identity.application.port.in.result;

import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;

import java.util.UUID;

public record UpdateDisplayNameResult(
        UUID userId,
        String displayName
) {
    public UpdateDisplayNameResult {
        ObjectPrecondition.requireNonNull(userId, "userId");
        StringPrecondition.requireNonBlank(displayName, "displayName");
    }

    public static UpdateDisplayNameResult of(UUID userId, String displayName) {
        return new UpdateDisplayNameResult(userId, displayName);
    }
}
