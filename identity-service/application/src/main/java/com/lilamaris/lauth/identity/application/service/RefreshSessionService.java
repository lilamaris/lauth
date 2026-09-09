package com.lilamaris.lauth.identity.application.service;

import com.lilamaris.lauth.identity.application.exception.IdentityServiceProgressCode;
import com.lilamaris.lauth.identity.application.internal.session.AccessTokenService;
import com.lilamaris.lauth.identity.application.internal.session.RefreshTokenService;
import com.lilamaris.lauth.identity.application.model.jwt.TokenPair;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenCodec;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenHasher;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenPurpose;
import com.lilamaris.lauth.identity.application.port.in.RefreshSessionUseCase;
import com.lilamaris.lauth.identity.application.port.in.command.RefreshSessionCommand;
import com.lilamaris.lauth.identity.application.port.out.RefreshTokenContextReader;
import com.lilamaris.lauth.identity.application.port.out.RefreshTokenStore;
import com.lilamaris.shrturl.kernel.application.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshSessionService implements RefreshSessionUseCase {
    private final OpaqueTokenHasher opaqueTokenHasher;
    private final RefreshTokenContextReader refreshTokenContextReader;
    private final RefreshTokenStore refreshTokenStore;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final Clock clock;

    @Override
    @Transactional
    public TokenPair refresh(RefreshSessionCommand command) {
        var opaqueToken = OpaqueTokenCodec.decode(command.token());

        var refreshTokenId = UUID.fromString(opaqueToken.selector());
        var context = refreshTokenContextReader.findByRefreshTokenId(refreshTokenId)
                .orElseThrow(() -> new ApplicationException(IdentityServiceProgressCode.SESSION_NOT_FOUND));

        var matched = opaqueTokenHasher.matches(OpaqueTokenPurpose.REFRESH_TOKEN, opaqueToken.value(), context.tokenHash());
        if (!matched)
            throw new ApplicationException(IdentityServiceProgressCode.TOKEN_VERIFICATION_FAILED);

        if (context.consumedAt() != null) {
            // 이미 사용된 토큰 재사용 감지 -> 해당 세션 만료 흐름
            throw new ApplicationException(null);
        }

        var issuedAt = clock.instant();
        var consumed = refreshTokenStore.consume(refreshTokenId, issuedAt);
        if (!consumed) throw new ApplicationException(IdentityServiceProgressCode.REFRESH_TOKEN_ALREADY_CONSUMED);

        var newRefreshToken = refreshTokenService.issue(context.session(), issuedAt);
        var newAccessToken = accessTokenService.issue(context.user(), context.session().sessionId(), issuedAt);

        return TokenPair.of(newAccessToken, newRefreshToken);
    }
}
