package com.lilamaris.lauth.kenel.web.response.error;

public interface ErrorCodeResolver {
    String resolve(ErrorDescriptor descriptor);
}
