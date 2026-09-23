package com.lilamaris.lauth.identity.security.config;

import com.lilamaris.lauth.identity.application.model.scope.ScopeCodec;
import com.lilamaris.lauth.identity.application.port.out.UserGrantReader;
import com.lilamaris.lauth.identity.security.method.credential.request.CredentialAuthenticateToken;
import com.lilamaris.lauth.identity.security.principal.CurrentUserPrincipal;
import com.lilamaris.lauth.identity.security.principal.SerializableUserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUserScopeTest {
    private final CustomOAuth2AuthorizationServerConfiguration configuration = new CustomOAuth2AuthorizationServerConfiguration();

    @ParameterizedTest
    @MethodSource("authentications")
    void credentialAndFederatedTokensOnlyGrantAuthorizedUserScopes(Authentication authentication) {
        UserGrantReader reader = userId -> {
            assertThat(userId.toString()).isEqualTo(authentication.getName());
            return Set.of(ScopeCodec.decode("user.read"), ScopeCodec.decode("other.write"));
        };
        var context = context(authentication, OAuth2TokenType.ACCESS_TOKEN, AuthorizationGrantType.AUTHORIZATION_CODE,
                Set.of("openid", "profile", "user.read", "user.write"));

        configuration.jwtEncodingContextOAuth2TokenCustomizer(reader).customize(context);

        var claims = context.getClaims().build();
        assertThat(claims.getClaimAsStringList("scope")).containsExactlyInAnyOrder("openid", "profile", "user.read");
        var jwt = Jwt.withTokenValue("test-token")
                .header("alg", "RS256")
                .claims(values -> values.putAll(claims.getClaims()))
                .build();
        var result = new GlobalSecurityConfiguration().jwtAuthenticationConverter().convert(jwt);
        assertThat(result).isNotNull();
        assertThat(result.getAuthorities())
                .filteredOn(authority -> authority.getAuthority().startsWith("SCOPE_"))
                .extracting("authority")
                .containsExactlyInAnyOrder("SCOPE_openid", "SCOPE_profile", "SCOPE_user.read");
        assertThat(result.getPrincipal()).isInstanceOf(CurrentUserPrincipal.class);
        var user = ((CurrentUserPrincipal) result.getPrincipal()).user();
        assertThat(user.userId().toString()).isEqualTo(authentication.getName());
        assertThat(user.displayName()).isEqualTo("test");
        assertThat(user.createdAt()).isNotNull();
        assertThat(user.updatedAt()).isNotNull();
    }

    @Test
    void refreshReReadsGrantsAndNeverAddsUnapprovedScopes() {
        var grants = new AtomicReference<>(Set.of(ScopeCodec.decode("user.read")));
        var customizer = configuration.jwtEncodingContextOAuth2TokenCustomizer(userId -> grants.get());
        var authentication = credential();
        var initial = context(authentication, OAuth2TokenType.ACCESS_TOKEN, AuthorizationGrantType.AUTHORIZATION_CODE,
                Set.of("openid", "user.read"));
        customizer.customize(initial);
        assertThat(initial.getClaims().build().getClaimAsStringList("scope")).containsExactlyInAnyOrder("openid", "user.read");

        grants.set(Set.of(ScopeCodec.decode("user.write")));
        var refreshed = context(authentication, OAuth2TokenType.ACCESS_TOKEN, AuthorizationGrantType.REFRESH_TOKEN,
                Set.of("openid", "user.read"));
        customizer.customize(refreshed);
        assertThat(refreshed.getClaims().build().getClaimAsStringList("scope")).containsExactly("openid");
    }

    @Test
    void noUserGrantsProduceNoResourceScopes() {
        var context = context(credential(), OAuth2TokenType.ACCESS_TOKEN, AuthorizationGrantType.AUTHORIZATION_CODE,
                Set.of("user.read", "user.write"));
        configuration.jwtEncodingContextOAuth2TokenCustomizer(userId -> Set.of()).customize(context);
        assertThat(context.getClaims().build().getClaimAsStringList("scope")).isEmpty();
    }

    @Test
    void scopesRemovedFromClientAreNotIssued() {
        var context = context(credential(), OAuth2TokenType.ACCESS_TOKEN, AuthorizationGrantType.REFRESH_TOKEN,
                Set.of("other.write"));
        configuration.jwtEncodingContextOAuth2TokenCustomizer(userId -> Set.of(ScopeCodec.decode("other.write")))
                .customize(context);
        assertThat(context.getClaims().build().getClaimAsStringList("scope")).isEmpty();
    }

    @Test
    void idTokensAndNonUserPrincipalsAreNotCustomized() {
        UserGrantReader reader = userId -> { throw new AssertionError("Must not query user grants"); };
        var customizer = configuration.jwtEncodingContextOAuth2TokenCustomizer(reader);
        var idToken = context(credential(), new OAuth2TokenType("id_token"), AuthorizationGrantType.AUTHORIZATION_CODE,
                Set.of("openid"));
        var clientToken = context(new UsernamePasswordAuthenticationToken("client", null, List.of()),
                OAuth2TokenType.ACCESS_TOKEN, AuthorizationGrantType.CLIENT_CREDENTIALS, Set.of("user.read"));
        for (var context : List.of(idToken, clientToken)) {
            var original = context.getClaims().build().getClaims();
            customizer.customize(context);
            assertThat(context.getClaims().build().getClaims()).isEqualTo(original);
        }
    }

    private JwtEncodingContext context(Authentication authentication, OAuth2TokenType tokenType,
                                       AuthorizationGrantType grantType, Set<String> scopes) {
        var client = RegisteredClient.withId("client")
                .clientId("client")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("https://client.example/callback")
                .scope("openid").scope("profile").scope("user.read").scope("user.write")
                .build();
        var now = Instant.now();
        return JwtEncodingContext.with(JwsHeader.with(SignatureAlgorithm.RS256), JwtClaimsSet.builder()
                        .subject(authentication.getName()).issuedAt(now).expiresAt(now.plusSeconds(300)).claim("scope", scopes))
                .registeredClient(client).principal(authentication).authorizedScopes(scopes)
                .tokenType(tokenType).authorizationGrantType(grantType).build();
    }

    private Authentication credential() {
        return new CredentialAuthenticateToken(SerializableUserPrincipal.of(UUID.randomUUID(), "test", Instant.now(), Instant.now()));
    }

    static Stream<Authentication> authentications() {
        return JdbcAuthorizationPersistenceTest.authentications();
    }
}
