package com.lilamaris.lauth.kenel.web.response.error;

import com.lilamaris.lauth.kernel.application.exception.ProcessReason;
import com.lilamaris.lauth.kernel.application.exception.ProgressType;

import java.util.Optional;

public interface ErrorDescriptor {
    ProcessReason reason();

    default Optional<String> resourceName() {
        return Optional.empty();
    }

    ProgressType type();

    String message();
}
