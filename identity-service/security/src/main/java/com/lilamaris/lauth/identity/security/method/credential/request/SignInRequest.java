package com.lilamaris.lauth.identity.security.method.credential.request;

import com.lilamaris.lauth.identity.security.method.credential.model.Credential;
import com.lilamaris.lauth.kernel.core.condition.StringPrecondition;

public record SignInRequest(
        String email,
        String password
) {
    public SignInRequest {
        StringPrecondition.requireNonBlank(email, "email");
        StringPrecondition.requireNonBlank(password, "password");
    }

    public static SignInRequest of(String email, String password) {
        return new SignInRequest(email, password);
    }

    public Credential toCredential() {
        return Credential.of(email, password);
    }
}
