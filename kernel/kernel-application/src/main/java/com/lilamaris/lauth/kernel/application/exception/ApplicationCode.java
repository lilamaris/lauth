package com.lilamaris.lauth.kernel.application.exception;

public interface ApplicationCode {
    ProcessReason reason();

    ProgressType type();

    String message();
}
