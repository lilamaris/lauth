package com.lilamaris.lauth.identity.application.internal.session;

import com.lilamaris.cozyr.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.lauth.identity.application.config.session.RefreshTokenProperties;
import com.lilamaris.lauth.identity.application.exception.IdentityServiceProgressCode;
import com.lilamaris.lauth.identity.application.model.jwt.TokenMetadata;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenCodec;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenGenerator;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenHasher;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenPurpose;
import com.lilamaris.lauth.identity.application.model.session.SessionContext;
import com.lilamaris.lauth.identity.application.port.out.RefreshTokenStore;
import com.lilamaris.lauth.identity.domain.RefreshToken;
import com.lilamaris.shrturl.kernel.application.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenProperties refreshTokenProperties;
    private final RefreshTokenStore refreshTokenStore;
    private final OpaqueTokenHasher opaqueTokenHasher;
    private final OpaqueTokenGenerator opaqueTokenGenerator;

    public TokenMetadata issue(SessionContext context, Instant issuedAt) {
        ObjectPrecondition.requireNonNull(context, "context");
        ObjectPrecondition.requireNonNull(issuedAt, "issuedAt");

        if (context.revokedAt() != null)
            throw new ApplicationException(IdentityServiceProgressCode.SESSION_ALREADY_REVOKED);

        var tokenExpiresAt = issuedAt.plus(refreshTokenProperties.expiration());
        var actualExpiresAt = context.expiresAt().isAfter(tokenExpiresAt) ? tokenExpiresAt : context.expiresAt();
        var tokenValue = opaqueTokenGenerator.generate();
        var tokenHash = opaqueTokenHasher.hash(OpaqueTokenPurpose.REFRESH_TOKEN, tokenValue);

        var refreshToken = RefreshToken.of(context.sessionId(), tokenHash, issuedAt, actualExpiresAt);
        var refreshTokenId = refreshTokenStore.save(refreshToken);

        var encoded = OpaqueTokenCodec.encode(refreshTokenId, tokenValue);

        return TokenMetadata.refreshToken(encoded, issuedAt, actualExpiresAt);
    }
}
