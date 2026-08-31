package com.lilamaris.shrturl.kernel.application.exception;

public interface ApplicationCode {
    ProcessReason reason();

    String type();

    String message();
}
