package com.lilamaris.lauth.identity.security.method.credential.model;

import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;

public record Credential(
        String email,
        String password
) {
    public Credential {
        StringPrecondition.requireNonBlank(email, "email");
        StringPrecondition.requireNonBlank(password, "password");
    }

    public static Credential of(String email, String password) {
        return new Credential(email, password);
    }
}
