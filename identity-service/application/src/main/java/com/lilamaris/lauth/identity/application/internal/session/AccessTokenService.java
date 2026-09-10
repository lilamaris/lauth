package com.lilamaris.lauth.identity.application.internal.session;

import com.lilamaris.cozyr.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.lauth.identity.application.config.session.AccessTokenProperties;
import com.lilamaris.lauth.identity.application.model.jwt.TokenMetadata;
import com.lilamaris.lauth.identity.application.model.scope.ScopeCodec;
import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccessTokenService {
    private final AccessTokenProperties accessTokenProperties;
    private final JwtEncoder jwtEncoder;

    public TokenMetadata issue(UserPrincipal principal, UUID sessionId, Instant issuedAt) {
        ObjectPrecondition.requireNonNull(principal, "principal");
        return create(principal, sessionId, issuedAt);
    }

    private TokenMetadata create(UserPrincipal principal, UUID sessionId, Instant issuedAt) {
        var expiresAt = issuedAt.plus(accessTokenProperties.expiration());

        var subject = principal.userId().toString();
        var scopes = ScopeCodec.encode(principal.granted().scopes());

        var claims = JwtClaimsSet.builder()
                .issuer(accessTokenProperties.issuer())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(subject)
                .claim("sid", sessionId)
                .claim("scope", scopes)
                .claim("display", principal.displayName())
                .build();

        var parameters = JwtEncoderParameters.from(claims);
        var value = jwtEncoder.encode(parameters).getTokenValue();

        return TokenMetadata.accessToken(value, issuedAt, expiresAt);
    }
}
