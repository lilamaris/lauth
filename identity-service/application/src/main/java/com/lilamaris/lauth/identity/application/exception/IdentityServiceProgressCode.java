package com.lilamaris.lauth.identity.application.exception;

import com.lilamaris.shrturl.kernel.application.exception.ApplicationProgressCode;
import com.lilamaris.shrturl.kernel.application.exception.ProcessReason;
import com.lilamaris.shrturl.kernel.application.exception.ProgressType;
import com.lilamaris.shrturl.kernel.application.exception.StandardProgressType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum IdentityServiceProgressCode implements ApplicationProgressCode {
    USER_NOT_FOUND(ProcessReason.REJECTED, "user", StandardProgressType.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    CREDENTIAL_NOT_FOUND(ProcessReason.REJECTED, "credential", StandardProgressType.NOT_FOUND, "계정을 찾을 수 없습니다."),
    SESSION_NOT_FOUND(ProcessReason.REJECTED, "session", StandardProgressType.NOT_FOUND, "세션을 찾을 수 없습니다."),
    EMAIL_DUPLICATED(ProcessReason.REJECTED, "credential", StandardProgressType.DUPLICATED, "이미 사용 중인 이메일입니다."),
    AUTHENTICATION_FAILED(ProcessReason.REJECTED, "credential", IdentityServiceProgressType.AUTHENTICATION_FAILED, "인증 실패."),

    SCOPE_GRANT_FAILED(ProcessReason.FAILURE, "scope", IdentityServiceProgressType.GRANT_FAILED, "권한 부여 실패.");

    private final ProcessReason reason;
    private final String resourceName;
    private final ProgressType type;
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
    public ProgressType type() {
        return type;
    }

    @Override
    public String message() {
        return message;
    }
}
