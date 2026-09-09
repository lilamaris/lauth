package com.lilamaris.lauth.identity.application.model.opaque;

public interface OpaqueTokenHasher {
    String hash(OpaqueTokenPurpose purpose, String token);

    boolean matches(OpaqueTokenPurpose purpose, String token, String expectedHash);
}
