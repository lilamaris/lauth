package com.lilamaris.lauth.identity.application.port.in.command;

import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;
import org.jspecify.annotations.Nullable;

public record ResolveFederatedAccountCommand(
        String registrationId,
        String providerUserId,
        @Nullable String providerUserNickname
) {
    public ResolveFederatedAccountCommand {
        StringPrecondition.requireNonBlank(registrationId, "registrationId");
        StringPrecondition.requireNonBlank(providerUserId, "providerUserId");
    }

    public static ResolveFederatedAccountCommand of(String registrationId, String providerUserId, @Nullable String providerUserNickname) {
        return new ResolveFederatedAccountCommand(registrationId, providerUserId, providerUserNickname);
    }
}
