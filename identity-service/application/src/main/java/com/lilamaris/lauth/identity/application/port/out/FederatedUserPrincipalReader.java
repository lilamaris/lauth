package com.lilamaris.lauth.identity.application.port.out;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;

import java.util.Optional;

public interface FederatedUserPrincipalReader {
    Optional<UserPrincipal> findByFederatedIdentity(String registrationId, String providerUserId);
}
