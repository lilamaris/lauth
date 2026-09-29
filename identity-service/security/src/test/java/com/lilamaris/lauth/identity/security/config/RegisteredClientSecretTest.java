package com.lilamaris.lauth.identity.security.config;

import org.junit.jupiter.api.Test;
import com.lilamaris.lauth.identity.application.config.TestUserProperties;
import java.nio.file.Path;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class RegisteredClientSecretTest {
    @Test
    void registeredClientSecretMatchesApplicationArgon2Encoder() {
        var encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        var repository = new CustomOAuth2AuthorizationServerConfiguration().registeredClientRepository(encoder,
                new TestUserProperties(false, 1000, Path.of("/tmp/test-ids"), null));
        var client = repository.findByClientId("oidc-client");

        assertThat(client).isNotNull();
        assertThat(client.getScopes()).containsExactlyInAnyOrder("openid", "profile", "user.read", "user.write");
        assertThat(client.getClientSecret()).startsWith("$argon2id$");
        assertThat(encoder.matches("secret", client.getClientSecret())).isTrue();
        assertThat(encoder.matches("wrong-secret", client.getClientSecret())).isFalse();
        assertThat(repository.findByClientId("test-client")).isNull();
    }

    @Test
    void testClientIsOnlyRegisteredWhenEnabled() {
        var encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        var repository = new CustomOAuth2AuthorizationServerConfiguration().registeredClientRepository(encoder,
                new TestUserProperties(true, 2, Path.of("/tmp/test-ids"), "configured-secret"));
        var client = repository.findByClientId("test-client");
        assertThat(client).isNotNull();
        assertThat(encoder.matches("configured-secret", client.getClientSecret())).isTrue();
    }
}
