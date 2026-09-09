package com.lilamaris.lauth.identity.application.service;

import com.lilamaris.lauth.identity.application.internal.session.AccessTokenService;
import com.lilamaris.lauth.identity.application.internal.session.RefreshTokenService;
import com.lilamaris.lauth.identity.application.internal.session.SessionService;
import com.lilamaris.lauth.identity.application.model.jwt.TokenPair;
import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.in.LoginSessionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class LoginSessionService implements LoginSessionUseCase {
    private final SessionService sessionService;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final Clock clock;

    @Override
    @Transactional
    public TokenPair login(UserPrincipal principal) {
        var createdAt = clock.instant();
        var session = sessionService.create(principal.userId(), createdAt);

        var accessToken = accessTokenService.issue(principal, session.sessionId(), createdAt);
        var refreshToken = refreshTokenService.issue(session, createdAt);

        return TokenPair.of(accessToken, refreshToken);
    }
}
