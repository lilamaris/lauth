package com.lilamaris.lauth.identity.security.test;

import com.lilamaris.lauth.identity.application.config.TestUserIds;
import com.lilamaris.lauth.identity.application.config.TestUserProperties;
import com.lilamaris.lauth.identity.application.model.scope.ScopeCodec;
import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.out.UserPrincipalReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContext;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TestGrantFlowTest {
    private final TestUserProperties properties = new TestUserProperties(true, 2, Path.of("/tmp/ids"), "test-secret");
    private final TestUserIds ids = new TestUserIds(properties);

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
        AuthorizationServerContextHolder.resetContext();
    }

    @Test
    void converterOnlyAcceptsTheTestGrantWithOneValidUserId() {
        var converter = new TestGrantAuthenticationConverter();
        var request = new MockHttpServletRequest("POST", "/oauth2/token");
        request.addParameter("grant_type", "client_credentials");
        assertThat(converter.convert(request)).isNull();

        request.setParameter("grant_type", TestGrantType.TEST_GRANT_TYPE.getValue());
        request.addParameter("user_id", "bad-uuid");
        assertThatThrownBy(() -> converter.convert(request)).isInstanceOf(OAuth2AuthenticationException.class);
        request.setParameter("user_id", ids.ids().getFirst().toString());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("client", "secret"));
        assertThat(converter.convert(request)).isInstanceOf(TestGrantAuthenticationToken.class);
    }

    @Test
    void providerRejectsUnseededIdsAndStoresIssuedAuthorization() {
        var client = RegisteredClient.withId("test-client")
                .clientId("test-client")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(TestGrantType.TEST_GRANT_TYPE)
                .scope("user.read").scope("user.write")
                .build();
        var clientAuth = new OAuth2ClientAuthenticationToken(client, ClientAuthenticationMethod.CLIENT_SECRET_BASIC, null);
        var userId = ids.ids().getFirst();
        var now = Instant.now();
        UserPrincipalReader reader = id -> Optional.of(UserPrincipal.of(id, "k6-user", now, now));
        var authorizationService = mock(OAuth2AuthorizationService.class);
        @SuppressWarnings("unchecked")
        OAuth2TokenGenerator<org.springframework.security.oauth2.core.OAuth2Token> generator = mock(OAuth2TokenGenerator.class);
        when(generator.generate(any())).thenAnswer(invocation -> {
            org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext context = invocation.getArgument(0);
            if (OAuth2TokenType.REFRESH_TOKEN.equals(context.getTokenType())) {
                return new OAuth2RefreshToken("refresh-token", now, now.plusSeconds(3600));
            }
            return Jwt.withTokenValue("test-token")
                    .header("alg", "RS256").subject(userId.toString())
                    .issuedAt(now).expiresAt(now.plusSeconds(300)).build();
        });
        var provider = new TestGrantedAuthenticationProvider(reader,
                id -> Set.of(ScopeCodec.decode("user.read")), ids, authorizationService, generator);
        AuthorizationServerContextHolder.setContext(mock(AuthorizationServerContext.class));

        assertThatThrownBy(() -> provider.authenticate(new TestGrantAuthenticationToken(UUID.randomUUID(), clientAuth)))
                .isInstanceOf(OAuth2AuthenticationException.class);
        verifyNoInteractions(authorizationService);

        var result = provider.authenticate(new TestGrantAuthenticationToken(userId, clientAuth));
        assertThat(result).isNotNull();
        assertThat(result.getCredentials()).isNotNull();
        var saved = org.mockito.ArgumentCaptor.forClass(OAuth2Authorization.class);
        verify(authorizationService).save(saved.capture());
        assertThat(saved.getValue().getPrincipalName()).isEqualTo(userId.toString());
        assertThat(saved.getValue().getAuthorizedScopes()).containsExactly("user.read");
        assertThat(saved.getValue().getAccessToken().getToken().getTokenValue()).isEqualTo("test-token");
        assertThat(saved.getValue().getRefreshToken().getToken().getTokenValue()).isEqualTo("refresh-token");
    }
}
