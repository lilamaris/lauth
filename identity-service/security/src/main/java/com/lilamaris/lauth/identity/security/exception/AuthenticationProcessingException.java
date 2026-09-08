package com.lilamaris.lauth.identity.security.exception;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationProcessingException extends AuthenticationException {
    public AuthenticationProcessingException(String message) {
        super(message);
    }

    public AuthenticationProcessingException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
