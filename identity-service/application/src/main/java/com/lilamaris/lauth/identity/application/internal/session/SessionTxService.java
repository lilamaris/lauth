package com.lilamaris.lauth.identity.application.internal.session;

import com.lilamaris.lauth.identity.application.exception.IdentityServiceProgressCode;
import com.lilamaris.lauth.identity.application.model.execute.ExecuteOutcome;
import com.lilamaris.lauth.identity.application.model.jwt.TokenPair;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenCodec;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenHasher;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenPurpose;
import com.lilamaris.lauth.identity.application.port.in.command.RefreshSessionCommand;
import com.lilamaris.lauth.identity.application.port.out.RefreshTokenContextReader;
import com.lilamaris.lauth.identity.application.port.out.RefreshTokenStore;
import com.lilamaris.lauth.identity.application.port.out.SessionStore;
import com.lilamaris.lauth.kernel.application.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SessionTxService {
    private final OpaqueTokenHasher opaqueTokenHasher;
    private final RefreshTokenContextReader refreshTokenContextReader;
    private final RefreshTokenStore refreshTokenStore;
    private final RefreshTokenService refreshTokenService;
    private final AccessTokenService accessTokenService;
    private final SessionStore sessionStore;
    private final Clock clock;

    @Transactional
    public ExecuteOutcome<TokenPair> executeRefreshSession(RefreshSessionCommand command) {
        var opaqueToken = OpaqueTokenCodec.decode(command.token());

        var refreshTokenId = UUID.fromString(opaqueToken.selector());
        var context = refreshTokenContextReader.findByRefreshTokenId(refreshTokenId)
                .orElseThrow(() -> new ApplicationException(IdentityServiceProgressCode.SESSION_NOT_FOUND));

        var now = clock.instant();
        if (context.session().revokedAt() != null)
            throw new ApplicationException(IdentityServiceProgressCode.SESSION_ALREADY_REVOKED);

        var matched = opaqueTokenHasher.matches(OpaqueTokenPurpose.REFRESH_TOKEN, opaqueToken.value(), context.tokenHash());
        if (!matched)
            throw new ApplicationException(IdentityServiceProgressCode.TOKEN_VERIFICATION_FAILED);

        if (context.consumedAt() != null) {
            var revoked = sessionStore.tryRevoke(context.session().sessionId(), now);
            if (!revoked) throw new ApplicationException(IdentityServiceProgressCode.SESSION_ALREADY_REVOKED);
            return ExecuteOutcome.failure(IdentityServiceProgressCode.TOKEN_REUSE_DETECTED);
        }

        if (!now.isBefore(context.session().expiresAt()))
            throw new ApplicationException(IdentityServiceProgressCode.SESSION_EXPIRED);
        if (!now.isBefore(context.expiresAt()))
            throw new ApplicationException(IdentityServiceProgressCode.TOKEN_EXPIRED);

        var consumed = refreshTokenStore.tryConsume(context.refreshTokenId(), now);
        if (!consumed) throw new ApplicationException(IdentityServiceProgressCode.REFRESH_TOKEN_ALREADY_CONSUMED);

        var newRefreshToken = refreshTokenService.issue(context.session(), now);
        var newAccessToken = accessTokenService.issue(context.user(), context.session().sessionId(), now);

        var data = TokenPair.of(newAccessToken, newRefreshToken);

        return ExecuteOutcome.success(data);
    }
}
