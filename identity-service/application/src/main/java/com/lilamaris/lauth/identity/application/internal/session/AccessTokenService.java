package com.lilamaris.lauth.identity.application.internal.session;

import com.lilamaris.lauth.identity.application.config.session.AccessTokenProperties;
import com.lilamaris.lauth.identity.application.model.jwt.TokenMetadata;
import com.lilamaris.lauth.identity.application.model.scope.ResourceScope;
import com.lilamaris.lauth.identity.application.model.scope.ScopeCodec;
import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.out.UserGrantReader;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccessTokenService {
    private final AccessTokenProperties accessTokenProperties;
    private final UserGrantReader userGrantReader;
    private final JwtEncoder jwtEncoder;

    public TokenMetadata issue(UserPrincipal principal, UUID sessionId, Instant issuedAt) {
        var userId = principal.userId();
        var userGrant = userGrantReader.findByUserId(userId);
        return create(principal, userGrant, sessionId, issuedAt);
    }

    private TokenMetadata create(UserPrincipal principal, Set<ResourceScope> grantedScopes, UUID sessionId, Instant issuedAt) {
        var expiresAt = issuedAt.plus(accessTokenProperties.expiration());

        var subject = principal.userId().toString();
        var scopes = ScopeCodec.encode(grantedScopes);

        var claims = JwtClaimsSet.builder()
                .issuer(accessTokenProperties.issuer())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(subject)
                .claim("sid", sessionId)
                .claim("scope", scopes)
                .claim("display", principal.displayName())
                .claim("createdAt", Date.from(principal.createdAt()))
                .claim("updatedAt", Date.from(principal.updatedAt()))
                .build();

        var parameters = JwtEncoderParameters.from(claims);
        var value = jwtEncoder.encode(parameters).getTokenValue();

        return TokenMetadata.accessToken(value, issuedAt, expiresAt);
    }
}
