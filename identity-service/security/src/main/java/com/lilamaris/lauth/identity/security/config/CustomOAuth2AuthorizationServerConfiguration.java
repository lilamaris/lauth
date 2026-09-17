package com.lilamaris.lauth.identity.security.config;

import com.lilamaris.lauth.identity.security.method.federated.resolver.FederatedUserPrincipal;
import com.lilamaris.lauth.identity.security.principal.SerializableUserPrincipal;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

@Configuration
public class CustomOAuth2AuthorizationServerConfiguration {
    @Bean
    JWKSource<SecurityContext> jwkSource(JWK activeJWK) {
        return (jwkSelector, context) -> jwkSelector.select(new JWKSet(activeJWK));
    }

    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> jwtEncodingContextOAuth2TokenCustomizer() {
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

            claims.claim("display", principal.displayName());
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
    SecurityFilterChain authorizationServerFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .oauth2AuthorizationServer(configurer -> {
                    httpSecurity.securityMatcher(configurer.getEndpointsMatcher());
                    configurer
                            .oidc(Customizer.withDefaults());
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
