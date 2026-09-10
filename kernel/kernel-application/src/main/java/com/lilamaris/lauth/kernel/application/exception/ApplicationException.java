package com.lilamaris.lauth.kernel.application.exception;

import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
    private final ApplicationCode applicationCode;

    public ApplicationException(ApplicationCode applicationCode) {
        super(ObjectPrecondition.requireNonNull(applicationCode, "code").message());
        this.applicationCode = applicationCode;
    }

    public ApplicationException(ApplicationCode applicationCode, Throwable e) {
        super(ObjectPrecondition.requireNonNull(applicationCode, "code").message(), e);
        this.applicationCode = applicationCode;
    }
}
