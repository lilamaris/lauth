package com.lilamaris.lauth.identity.application.config;

import com.lilamaris.lauth.identity.application.internal.jwks.JWKBuilder;
import com.lilamaris.lauth.identity.application.internal.jwks.JWKSRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.util.List;

@Configuration
@EnableConfigurationProperties({
        ApplicationProperties.class,
        JwtProperties.class,
        ScopeProperties.class,
        JWKSProperties.class
})
public class ApplicationConfiguration {
    @Bean
    Clock clock(ApplicationProperties properties) {
        return Clock.system(properties.timezone());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    JWKSRegistry jwksRegistry(List<JWKBuilder<?>> jwkBuilders) {
        return new JWKSRegistry(jwkBuilders);
    }
}
