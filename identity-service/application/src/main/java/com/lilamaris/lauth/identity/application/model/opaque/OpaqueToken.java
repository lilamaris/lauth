package com.lilamaris.lauth.identity.application.model.opaque;

import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;

public record OpaqueToken(
        String selector,
        String value
) {
    public OpaqueToken {
        StringPrecondition.requireNonBlank(selector, "selector");
        StringPrecondition.requireNonBlank(value, "value");
    }

    public static OpaqueToken of(String selector, String value) {
        return new OpaqueToken(selector, value);
    }
}
