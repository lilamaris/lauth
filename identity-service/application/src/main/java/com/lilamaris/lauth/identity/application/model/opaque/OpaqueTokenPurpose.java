package com.lilamaris.lauth.identity.application.model.opaque;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OpaqueTokenPurpose {
    REFRESH_TOKEN("refresh-token");

    private final String canonicalName;
}
