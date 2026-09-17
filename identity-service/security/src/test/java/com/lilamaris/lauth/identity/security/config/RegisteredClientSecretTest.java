package com.lilamaris.lauth.identity.security.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class RegisteredClientSecretTest {
    @Test
    void registeredClientSecretMatchesApplicationArgon2Encoder() {
        var encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        var repository = new GlobalSecurityConfiguration().registeredClientRepository(encoder);
        var client = repository.findByClientId("oidc-client");

        assertThat(client).isNotNull();
        assertThat(client.getClientSecret()).startsWith("$argon2id$");
        assertThat(encoder.matches("secret", client.getClientSecret())).isTrue();
        assertThat(encoder.matches("wrong-secret", client.getClientSecret())).isFalse();
    }
}
