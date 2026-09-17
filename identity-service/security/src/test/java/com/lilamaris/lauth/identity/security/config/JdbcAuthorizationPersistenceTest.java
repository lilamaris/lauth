package com.lilamaris.lauth.identity.security.config;

import com.lilamaris.lauth.identity.security.method.credential.request.CredentialAuthenticateToken;
import com.lilamaris.lauth.identity.security.method.federated.resolver.OAuth2FederatedUserPrincipal;
import com.lilamaris.lauth.identity.security.method.federated.resolver.OidcFederatedUserPrincipal;
import com.lilamaris.lauth.identity.security.principal.SerializableUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcAuthorizationPersistenceTest {
    private JdbcTemplate jdbc;

    @BeforeEach
    void createDatabase() {
        var dataSource = new DriverManagerDataSource("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1", "sa", "");
        new ResourceDatabasePopulator(new ClassPathResource(
                "org/springframework/security/oauth2/server/authorization/oauth2-authorization-schema.sql"))
                .execute(dataSource);
        jdbc = new JdbcTemplate(dataSource);
    }

    @ParameterizedTest
    @MethodSource("authentications")
    void refreshTokenAndPrincipalSurviveServiceAndClientRepositoryRecreation(Authentication authentication) {
        var encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        var security = new GlobalSecurityConfiguration();
        var configuration = new CustomOAuth2AuthorizationServerConfiguration();
        var clients = security.registeredClientRepository(encoder);
        var client = clients.findByClientId("oidc-client");
        assertThat(client).isNotNull();
        var issuedAt = Instant.now();
        var refreshToken = new OAuth2RefreshToken("refresh-" + UUID.randomUUID(), issuedAt, issuedAt.plusSeconds(3600));
        var authorization = OAuth2Authorization.withRegisteredClient(client)
                .principalName(authentication.getName())
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizedScopes(java.util.Set.of("openid", "profile"))
                .attribute(Principal.class.getName(), authentication)
                .refreshToken(refreshToken)
                .build();
        configuration.oAuth2AuthorizationService(jdbc, clients).save(authorization);

        var recreatedClients = security.registeredClientRepository(encoder);
        var recreatedService = configuration.oAuth2AuthorizationService(jdbc, recreatedClients);
        var restored = recreatedService.findByToken(refreshToken.getTokenValue(), OAuth2TokenType.REFRESH_TOKEN);
        assertThat(restored).isNotNull();
        assertThat(restored.getRegisteredClientId()).isEqualTo(client.getId());
        assertThat(restored.getRefreshToken().getToken().getTokenValue()).isEqualTo(refreshToken.getTokenValue());
        Authentication restoredAuthentication = restored.getAttribute(Principal.class.getName());
        assertThat(restoredAuthentication).isNotNull();
        assertThat(restoredAuthentication.isAuthenticated()).isTrue();
        assertThat(restoredAuthentication.getName()).isEqualTo(authentication.getName());
        assertThat(restoredAuthentication.getPrincipal()).isInstanceOf(authentication.getPrincipal().getClass());
        assertThat(restoredAuthentication.getPrincipal()).usingRecursiveComparison().isEqualTo(authentication.getPrincipal());
        assertThat(restoredAuthentication.getAuthorities()).isEqualTo(authentication.getAuthorities());
        assertThat(restoredAuthentication.getDetails()).isEqualTo(authentication.getDetails());
        if (restoredAuthentication instanceof OAuth2AuthenticationToken oauth2) {
            assertThat(oauth2.getAuthorizedClientRegistrationId()).isEqualTo("provider");
        }
    }

    static Stream<Authentication> authentications() {
        var user = SerializableUserPrincipal.of(UUID.randomUUID(), "test", Instant.now(), Instant.now());
        var authorities = List.of(new SimpleGrantedAuthority("SCOPE_profile"));
        var oauth2 = new OAuth2FederatedUserPrincipal(authorities, Map.of("login", "external-user"), "login", user);
        var now = Instant.now();
        var idToken = new OidcIdToken("id-token", now, now.plusSeconds(300),
                Map.of("sub", "external-subject", "preferred_username", "external-user"));
        var userInfo = new OidcUserInfo(Map.of("sub", "external-subject", "email", "user@example.com"));
        var oidc = new OidcFederatedUserPrincipal(authorities, idToken, userInfo, "preferred_username", user);
        var oidcWithoutUserInfo = new OidcFederatedUserPrincipal(authorities, idToken, null, "sub", user);
        return Stream.of(new CredentialAuthenticateToken(user),
                new OAuth2AuthenticationToken(oauth2, authorities, "provider"),
                new OAuth2AuthenticationToken(oidc, authorities, "provider"),
                new OAuth2AuthenticationToken(oidcWithoutUserInfo, authorities, "provider"))
                .peek(token -> token.setDetails("request-details"))
                .map(token -> (Authentication) token);
    }
}
