package com.lilamaris.shrturl.kernel.application.exception;

public interface ApplicationCode {
    ProcessReason reason();

    ProgressType type();

    String message();
}
