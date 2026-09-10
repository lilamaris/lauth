package com.lilamaris.lauth.identity.application.service;

import com.lilamaris.lauth.identity.application.exception.IdentityServiceProgressCode;
import com.lilamaris.lauth.identity.application.internal.session.SessionTxService;
import com.lilamaris.lauth.identity.application.model.jwt.TokenPair;
import com.lilamaris.lauth.identity.application.port.in.RefreshSessionUseCase;
import com.lilamaris.lauth.identity.application.port.in.command.RefreshSessionCommand;
import com.lilamaris.lauth.kernel.application.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshSessionService implements RefreshSessionUseCase {
    private final SessionTxService sessionTxService;

    @Override
    public TokenPair refresh(RefreshSessionCommand command) {
        var outcome = sessionTxService.executeRefreshSession(command);

        if (!outcome.success())
            throw new ApplicationException(outcome.code());

        return outcome.data()
                .orElseThrow(() -> new ApplicationException(IdentityServiceProgressCode.EXECUTE_OUTCOME_VIOLATION));
    }
}
