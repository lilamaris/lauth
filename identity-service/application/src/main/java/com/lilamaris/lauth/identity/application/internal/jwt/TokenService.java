package com.lilamaris.lauth.identity.application.internal.jwt;

import com.lilamaris.cozyr.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.lauth.identity.application.config.JwtProperties;
import com.lilamaris.lauth.identity.application.internal.random.RandomBase64URL;
import com.lilamaris.lauth.identity.application.model.jwt.TokenMetadata;
import com.lilamaris.lauth.identity.application.model.scope.ScopeCodec;
import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
@RequiredArgsConstructor
public class TokenService {
    private final JwtProperties properties;
    private final JwtEncoder jwtEncoder;
    private final RandomBase64URL randomString;
    private final Clock clock;

    public TokenMetadata createAccessToken(UserPrincipal principal) {
        ObjectPrecondition.requireNonNull(principal, "principal");

        var props = properties.accessToken();
        var issuedAt = clock.instant();
        var expiresAt = issuedAt.plus(props.expiration());

        var subject = principal.userId().toString();
        var scopes = ScopeCodec.encode(principal.granted().scopes());

        var claims = JwtClaimsSet.builder()
                .issuer(props.issuer())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(subject)
                .claim("scopes", scopes)
                .claim("display", principal.displayName())
                .build();

        var parameters = JwtEncoderParameters.from(claims);
        var value = jwtEncoder.encode(parameters).getTokenValue();

        return TokenMetadata.accessToken(value, issuedAt, expiresAt);
    }

    public TokenMetadata createRefreshToken() {
        var props = properties.refreshToken();
        var issuedAt = clock.instant();
        var expiresAt = issuedAt.plus(props.expiration());
        var value = randomString.generate();

        return TokenMetadata.refreshToken(value, issuedAt, expiresAt);
    }
}
