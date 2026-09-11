package com.lilamaris.lauth.identity.application.service;

import com.lilamaris.lauth.identity.application.exception.IdentityServiceProgressCode;
import com.lilamaris.lauth.identity.application.port.in.UpdateDisplayNameUseCase;
import com.lilamaris.lauth.identity.application.port.in.command.UpdateDisplayNameCommand;
import com.lilamaris.lauth.identity.application.port.in.result.UpdateDisplayNameResult;
import com.lilamaris.lauth.identity.application.port.out.UserMetadataStore;
import com.lilamaris.lauth.identity.application.port.out.status.UpdateDisplayNameStatus;
import com.lilamaris.lauth.kernel.application.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Clock;

@Service
@Validated
@RequiredArgsConstructor
public class UpdateDisplayNameService implements UpdateDisplayNameUseCase {
    private final UserMetadataStore userMetadataStore;
    private final Clock clock;

    public UpdateDisplayNameResult update(UpdateDisplayNameCommand command) {
        var userId = command.userId();
        var displayName = command.displayName();
        var updatedAt = clock.instant();
        var updateStatus = userMetadataStore.updateDisplayName(userId, displayName, updatedAt);
        if (updateStatus == UpdateDisplayNameStatus.USER_NOT_FOUND)
            throw new ApplicationException(IdentityServiceProgressCode.USER_NOT_FOUND);
        return UpdateDisplayNameResult.of(userId, displayName);
    }
}
