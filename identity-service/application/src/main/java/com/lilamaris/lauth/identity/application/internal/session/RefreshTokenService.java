package com.lilamaris.lauth.identity.application.internal.session;

import com.lilamaris.lauth.identity.application.config.session.RefreshTokenProperties;
import com.lilamaris.lauth.identity.application.model.jwt.TokenMetadata;
import com.lilamaris.lauth.identity.application.model.opaque.*;
import com.lilamaris.lauth.identity.application.port.out.RefreshTokenStore;
import com.lilamaris.lauth.identity.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenProperties refreshTokenProperties;
    private final RefreshTokenStore refreshTokenStore;
    private final OpaqueTokenHasher opaqueTokenHasher;
    private final OpaqueTokenGenerator opaqueTokenGenerator;

    public TokenMetadata create(UUID sessionId, Instant issuedAt) {
        var expiresAt = issuedAt.plus(refreshTokenProperties.expiration());
        var tokenValue = opaqueTokenGenerator.generate();
        var tokenHash = opaqueTokenHasher.hash(OpaqueTokenPurpose.REFRESH_TOKEN, tokenValue);

        var refreshToken = RefreshToken.of(sessionId, tokenHash, issuedAt, expiresAt);
        var refreshTokenId = refreshTokenStore.save(refreshToken);

        var encoded = OpaqueTokenCodec.encode(refreshTokenId, tokenValue);

        return TokenMetadata.refreshToken(encoded, issuedAt, expiresAt);
    }
}
