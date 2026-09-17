package com.lilamaris.lauth.identity.security.config;

import com.lilamaris.lauth.identity.security.method.credential.request.CredentialAuthenticateToken;
import com.lilamaris.lauth.identity.security.principal.SerializableUserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.io.*;
import java.security.Principal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CredentialAuthorizationJsonTest {
    @Test
    void jdbcAuthorizationAttributesRestoreAuthenticatedCredentialToken() {
        var mapper = CredentialAuthorizationJson.mapper();
        var principal = principal();
        var token = new CredentialAuthenticateToken(principal);
        token.setDetails("request-details");
        // JDBC authorization attributes contain the authentication under Principal's class name.
        var json = mapper.writeValueAsString(new java.util.HashMap<>(Map.of(Principal.class.getName(), token)));
        var type = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
        Map<String, Object> restored = mapper.readValue(json, type);
        var authentication = (Authentication) restored.get(Principal.class.getName());
        assertThat(authentication).isInstanceOf(CredentialAuthenticateToken.class);
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getPrincipal()).isEqualTo(principal);
        assertThat(authentication.getName()).isEqualTo(principal.userId().toString());
        assertThat(authentication.getCredentials()).isNull();
        assertThat(authentication.getAuthorities()).isEmpty();
        assertThat(authentication.getDetails()).isEqualTo("request-details");
        assertThat(json).doesNotContain("credentials");
    }

    @Test
    void authenticatedTokenAlsoSupportsJdbcSessionJavaSerialization() throws Exception {
        var token = new CredentialAuthenticateToken(principal());
        var bytes = new ByteArrayOutputStream();
        try (var out = new ObjectOutputStream(bytes)) { out.writeObject(token); }
        try (var in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            var restored = (Authentication) in.readObject();
            assertThat(restored.getPrincipal()).isEqualTo(token.getPrincipal());
            assertThat(restored.isAuthenticated()).isTrue();
            assertThat(restored.getCredentials()).isNull();
        }
    }

    private SerializableUserPrincipal principal() {
        return SerializableUserPrincipal.of(UUID.randomUUID(), "test", Instant.now(), Instant.now());
    }
}
