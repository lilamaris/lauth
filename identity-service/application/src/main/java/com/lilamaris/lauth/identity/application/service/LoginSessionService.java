package com.lilamaris.lauth.identity.application.service;

import com.lilamaris.lauth.identity.application.config.session.SessionProperties;
import com.lilamaris.lauth.identity.application.internal.session.AccessTokenService;
import com.lilamaris.lauth.identity.application.internal.session.RefreshTokenService;
import com.lilamaris.lauth.identity.application.model.jwt.TokenPair;
import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.in.LoginSessionUseCase;
import com.lilamaris.lauth.identity.application.port.out.SessionStore;
import com.lilamaris.lauth.identity.domain.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class LoginSessionService implements LoginSessionUseCase {
    private final SessionProperties sessionProperties;
    private final SessionStore sessionStore;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final Clock clock;

    @Override
    @Transactional
    public TokenPair login(UserPrincipal principal) {
        var createdAt = clock.instant();
        var expiresAt = createdAt.plus(sessionProperties.expiration());
        var session = Session.of(principal.userId(), "unknown", createdAt, expiresAt);
        var sessionId = sessionStore.save(session);

        var accessToken = accessTokenService.create(principal, sessionId, createdAt);
        var refreshToken = refreshTokenService.create(sessionId, createdAt);

        return TokenPair.of(accessToken, refreshToken);
    }
}
