package com.lilamaris.lauth.identity.security.method.federated.resolver;

import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;
import org.jspecify.annotations.Nullable;

public record FederatedIdentity(
        String registrationId,
        String providerUserId,
        @Nullable String providerUserNickname
) {
    public FederatedIdentity {
        StringPrecondition.requireNonBlank(registrationId, "registrationId");
        StringPrecondition.requireNonBlank(providerUserId, "providerUserId");
    }

    public static FederatedIdentity of(String registrationId, String providerUserId, @Nullable String providerUserNickname) {
        return new FederatedIdentity(registrationId, providerUserId, providerUserNickname);
    }
}
