package com.lilamaris.lauth.identity.application.service;

import com.lilamaris.lauth.identity.application.exception.IdentityServiceProgressCode;
import com.lilamaris.lauth.identity.application.port.in.UpdateHandleUseCase;
import com.lilamaris.lauth.identity.application.port.in.command.UpdateHandleCommand;
import com.lilamaris.lauth.identity.application.port.in.result.UpdateHandleResult;
import com.lilamaris.lauth.identity.application.port.out.UserMetadataStore;
import com.lilamaris.lauth.identity.application.port.out.status.UpdateHandleStatus;
import com.lilamaris.lauth.kernel.application.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Clock;

@Service
@Validated
@RequiredArgsConstructor
public class UpdateHandleService implements UpdateHandleUseCase {
    private final UserMetadataStore userMetadataStore;
    private final Clock clock;

    @Override
    @Transactional
    public UpdateHandleResult update(UpdateHandleCommand command) {
        var userId = command.userId();
        var handle = command.handle();
        var updatedAt = clock.instant();
        var updateStatus = userMetadataStore.updateHandle(userId, handle, updatedAt);
        if (updateStatus == UpdateHandleStatus.USER_NOT_FOUND)
            throw new ApplicationException(IdentityServiceProgressCode.USER_NOT_FOUND);
        if (updateStatus == UpdateHandleStatus.HANDLE_ALREADY_IN_USE)
            throw new ApplicationException(IdentityServiceProgressCode.USER_HANDLE_ALREADY_IN_USE);
        return UpdateHandleResult.of(userId, handle);
    }
}
