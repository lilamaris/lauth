package com.lilamaris.lauth.identity.security.config;

import com.lilamaris.lauth.identity.application.model.scope.ScopeCodec;
import com.lilamaris.lauth.identity.application.config.TestUserIds;
import com.lilamaris.lauth.identity.application.config.TestUserProperties;
import com.lilamaris.lauth.identity.application.port.out.UserGrantReader;
import com.lilamaris.lauth.identity.application.port.out.UserPrincipalReader;
import com.lilamaris.lauth.identity.security.method.federated.resolver.FederatedUserPrincipal;
import com.lilamaris.lauth.identity.security.principal.SerializableUserPrincipal;
import com.lilamaris.lauth.identity.security.test.TestGrantType;
import com.lilamaris.lauth.identity.security.test.TestGrantAuthenticationConverter;
import com.lilamaris.lauth.identity.security.test.TestGrantedAuthenticationProvider;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import java.util.Date;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Configuration
public class CustomOAuth2AuthorizationServerConfiguration {
    @Bean
    JWKSource<SecurityContext> jwkSource(JWK activeJWK) {
        return (jwkSelector, context) -> jwkSelector.select(new JWKSet(activeJWK));
    }

    @Bean
    RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder, TestUserProperties properties) {
        RegisteredClient oidcClient = RegisteredClient.withId("oidc-client")
                .clientId("oidc-client")
                .clientSecret(passwordEncoder.encode("secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("https://app.apidog.com/oauth2-browser-callback.html")
                .redirectUri("http://localhost:5174/auth/callback")
                .postLogoutRedirectUri("http://127.0.0.1:8080/")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("user.read")
                .scope("user.write")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();

        var clients = new ArrayList<RegisteredClient>();
        clients.add(oidcClient);
        if (properties.enabled()) {
            clients.add(RegisteredClient.withId("test-client")
                    .clientId("test-client")
                    .clientSecret(passwordEncoder.encode(properties.clientSecret()))
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(TestGrantType.TEST_GRANT_TYPE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .scope("user.read")
                    .scope("user.write")
                    .build());
        }
        return new InMemoryRegisteredClientRepository(clients);
    }

    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> jwtEncodingContextOAuth2TokenCustomizer(UserGrantReader userGrantReader) {
        return context -> {
            if (!OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) return;
            if (context.getPrincipal() == null) return;

            SerializableUserPrincipal principal = switch (context.getPrincipal().getPrincipal()) {
                case SerializableUserPrincipal user -> user;
                case FederatedUserPrincipal federated -> federated.user();
                case null, default -> null;
            };

            if (principal == null) return;

            var claims = context.getClaims();
            var userScopes = ScopeCodec.encode(userGrantReader.findByUserId(principal.userId()));
            // OIDC scopes describe identity access; resource scopes require a current user grant.
            var scopes = context.getAuthorizedScopes().stream()
                    .filter(context.getRegisteredClient().getScopes()::contains)
                    .filter(scope -> OidcScopes.OPENID.equals(scope)
                            || OidcScopes.PROFILE.equals(scope)
                            || userScopes.contains(scope))
                    .collect(Collectors.toSet());

            claims.claim("scope", scopes);
            claims.claim("display", principal.displayName());
            claims.claim("createdAt", Date.from(principal.createdAt()));
            claims.claim("updatedAt", Date.from(principal.updatedAt()));
        };
    }

    @Bean
    SessionRegistry sessionRegistry(JdbcIndexedSessionRepository sessionRepository) {
        return new SpringSessionBackedSessionRegistry<>(sessionRepository);
    }

    @Bean
    OAuth2AuthorizationService oAuth2AuthorizationService(JdbcOperations jdbcOperations, RegisteredClientRepository registeredClientRepository) {
        var mapper = CredentialAuthorizationJson.mapper();
        var service = new JdbcOAuth2AuthorizationService(jdbcOperations, registeredClientRepository);
        service.setAuthorizationRowMapper(
                new JdbcOAuth2AuthorizationService.JsonMapperOAuth2AuthorizationRowMapper(registeredClientRepository, mapper));
        service.setAuthorizationParametersMapper(
                new JdbcOAuth2AuthorizationService.JsonMapperOAuth2AuthorizationParametersMapper(mapper));
        return service;
    }

    @Bean
    @Order(1)
    SecurityFilterChain authorizationServerFilterChain(
            HttpSecurity httpSecurity,
            TestUserProperties properties,
            ObjectProvider<TestUserIds> testUserIds,
            UserPrincipalReader userPrincipalReader,
            UserGrantReader userGrantReader,
            OAuth2AuthorizationService authorizationService,
            JwtEncoder jwtEncoder,
            OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer
    ) throws Exception {
        httpSecurity
                .oauth2AuthorizationServer(configurer -> {
                    httpSecurity.securityMatcher(configurer.getEndpointsMatcher());
                    configurer
                            .oidc(Customizer.withDefaults());
                    if (properties.enabled()) {
                        var jwtGenerator = new JwtGenerator(jwtEncoder);
                        jwtGenerator.setJwtCustomizer(jwtCustomizer);
                        var tokenGenerator = new DelegatingOAuth2TokenGenerator(jwtGenerator, new OAuth2RefreshTokenGenerator());
                        configurer.tokenEndpoint(endpoint -> endpoint
                                .accessTokenRequestConverter(new TestGrantAuthenticationConverter())
                                .authenticationProvider(new TestGrantedAuthenticationProvider(
                                        userPrincipalReader, userGrantReader, testUserIds.getObject(),
                                        authorizationService, tokenGenerator)));
                    }
                })
                .authorizeHttpRequests(customizer ->
                        customizer
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                );

        return httpSecurity.build();
    }
}
