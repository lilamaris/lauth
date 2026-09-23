package com.lilamaris.lauth.identity.security.method.federated.resolver;

import com.lilamaris.lauth.identity.security.principal.SerializableUserPrincipal;

public interface FederatedUserPrincipal {
    SerializableUserPrincipal user();
}
