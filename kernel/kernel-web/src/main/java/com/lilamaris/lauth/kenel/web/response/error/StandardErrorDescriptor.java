package com.lilamaris.lauth.kenel.web.response.error;

import com.lilamaris.lauth.kernel.application.exception.ProcessReason;
import com.lilamaris.lauth.kernel.application.exception.ProgressType;
import com.lilamaris.lauth.kernel.application.exception.StandardProgressType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum StandardErrorDescriptor implements ErrorDescriptor {
    BAD_REQUEST(ProcessReason.REJECTED, StandardProgressType.BAD_REQUEST, "Bad Request"),
    NOT_FOUND(ProcessReason.REJECTED, StandardProgressType.NOT_FOUND, "Not Found"),
    CONFLICT(ProcessReason.REJECTED, StandardProgressType.DUPLICATED, "Duplicated"),
    ACCESS_DENIED(ProcessReason.REJECTED, StandardProgressType.ACCESS_DENIED, "Access Denied"),
    UNAUTHORIZED(ProcessReason.REJECTED, StandardProgressType.UNAUTHORIZED, "Unauthorized"),
    INTERNAL_SERVER_ERROR(ProcessReason.FAILURE, StandardProgressType.FAILED, "Internal server error");

    private final ProcessReason reason;
    private final StandardProgressType type;
    private final String message;

    @Override
    public ProcessReason reason() {
        return reason;
    }

    @Override
    public ProgressType type() {
        return type;
    }

    @Override
    public String message() {
        return message;
    }
}
