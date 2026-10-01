package com.lilamaris.lauth.identity.security.test;

import com.lilamaris.lauth.identity.application.model.scope.ScopeCodec;
import com.lilamaris.lauth.identity.application.config.TestUserIds;
import com.lilamaris.lauth.identity.application.port.out.UserGrantReader;
import com.lilamaris.lauth.identity.application.port.out.UserPrincipalReader;
import com.lilamaris.lauth.identity.security.method.credential.request.CredentialAuthenticateToken;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import java.security.Principal;

@NullMarked
@RequiredArgsConstructor
public class TestGrantedAuthenticationProvider implements AuthenticationProvider {
    private final UserPrincipalReader userPrincipalReader;
    private final UserGrantReader userGrantReader;
    private final TestUserIds testUserIds;
    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var grant = (TestGrantAuthenticationToken) authentication;

        if (!(grant.getPrincipal() instanceof OAuth2ClientAuthenticationToken client)) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
        }

        if (!client.isAuthenticated()) throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);

        var registeredClient = client.getRegisteredClient();

        if (registeredClient == null || !registeredClient.getAuthorizationGrantTypes().contains(TestGrantType.TEST_GRANT_TYPE)) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }

        if (!testUserIds.contains(grant.userId())) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_GRANT);
        }

        var user = userPrincipalReader.findPrincipalById(grant.userId())
                .orElseThrow(() -> new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_GRANT));
        var userAuthentication = CredentialAuthenticateToken.of(user);
        var allowedScopes = ScopeCodec.encode(userGrantReader.findByUserId(grant.userId()));
        var scopes = registeredClient.getScopes().stream()
                .filter(allowedScopes::contains)
                .collect(java.util.stream.Collectors.toSet());
        var tokenContext = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(userAuthentication)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorizedScopes(scopes)
                .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                .authorizationGrantType(TestGrantType.TEST_GRANT_TYPE)
                .authorizationGrant(grant)
                .build();

        var generatedToken = tokenGenerator.generate(tokenContext);

        if (generatedToken == null) throw new OAuth2AuthenticationException(
                new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR, "The token generator failed to generate the access token.", null)
        );

        var accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                generatedToken.getTokenValue(),
                generatedToken.getIssuedAt(),
                generatedToken.getExpiresAt(),
                scopes
        );

        var refreshContext = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(userAuthentication)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorizedScopes(scopes)
                .tokenType(OAuth2TokenType.REFRESH_TOKEN)
                .authorizationGrantType(TestGrantType.TEST_GRANT_TYPE)
                .authorizationGrant(grant)
                .build();
        if (!(tokenGenerator.generate(refreshContext) instanceof OAuth2RefreshToken refreshToken)) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.SERVER_ERROR);
        }

        var authorizationBuilder = OAuth2Authorization
                .withRegisteredClient(registeredClient)
                .principalName(user.userId().toString())
                .authorizationGrantType(TestGrantType.TEST_GRANT_TYPE)
                .authorizedScopes(scopes)
                .attribute(Principal.class.getName(), userAuthentication)
                .refreshToken(refreshToken);

        if (generatedToken instanceof ClaimAccessor claimAccessor) {
            authorizationBuilder.token(
                    accessToken,
                    metadata -> metadata.put(
                            OAuth2Authorization.Token.CLAIMS_METADATA_NAME,
                            claimAccessor.getClaims()
                    )
            );
        } else {
            authorizationBuilder.accessToken(accessToken);
        }

        var authorization = authorizationBuilder.build();

        authorizationService.save(authorization);

        return new OAuth2AccessTokenAuthenticationToken(
                registeredClient,
                client,
                accessToken,
                refreshToken
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TestGrantAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
