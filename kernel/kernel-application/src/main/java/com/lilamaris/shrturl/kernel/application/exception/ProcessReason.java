package com.lilamaris.shrturl.kernel.application.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProcessReason {
    SUCCESS("success"),
    FAILURE("failure"),
    REJECTED("rejected");

    private final String canonicalName;
}
