package com.lilamaris.lauth.identity.application.exception;

import com.lilamaris.shrturl.kernel.application.exception.ApplicationProgressCode;
import com.lilamaris.shrturl.kernel.application.exception.ProcessReason;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum IdentityServiceProgressCode implements ApplicationProgressCode {
    USER_NOT_FOUND(ProcessReason.REJECTED, "user", "not-found", "사용자를 찾을 수 없습니다."),
    CREDENTIAL_NOT_FOUND(ProcessReason.REJECTED, "credential", "not-found", "계정을 찾을 수 없습니다."),
    EMAIL_DUPLICATED(ProcessReason.REJECTED, "credential", "duplicated", "이미 사용 중인 이메일입니다."),
    AUTHENTICATION_FAILED(ProcessReason.REJECTED, "credential", "unauthorized", "인증 실패."),

    SCOPE_GRANT_FAILED(ProcessReason.FAILURE, "scope", "unauthorized", "권한 부여 실패.");


    private final ProcessReason reason;
    private final String resourceName;
    private final String type;
    private final String message;

    @Override
    public String resourceName() {
        return resourceName;
    }

    @Override
    public ProcessReason reason() {
        return reason;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String message() {
        return message;
    }
}
