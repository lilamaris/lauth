package com.lilamaris.lauth.identity.application.config;

import com.lilamaris.lauth.identity.application.config.session.AccessTokenProperties;
import com.lilamaris.lauth.identity.application.config.session.RefreshTokenProperties;
import com.lilamaris.lauth.identity.application.config.session.SessionProperties;
import com.lilamaris.lauth.identity.application.internal.jwks.JWKBuilder;
import com.lilamaris.lauth.identity.application.internal.jwks.JWKSRegistry;
import com.lilamaris.lauth.identity.application.internal.random.RandomDisplayName;
import com.lilamaris.lauth.identity.application.model.opaque.HmacSha256paqueTokenHasher;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenHasher;
import com.lilamaris.lauth.identity.application.port.out.JWKSReader;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.List;

@Configuration
@EnableConfigurationProperties({
        ApplicationProperties.class,
        ScopeProperties.class,
        JWKSProperties.class,
        PolicyProperties.class,
        SessionProperties.class,
        AccessTokenProperties.class,
        RefreshTokenProperties.class
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
    JWKSRegistry jwksRegistry(List<JWKBuilder<?>> jwkBuilders, JWKSReader jwksReader, JWKSProperties properties) {
        var registry = new JWKSRegistry(jwkBuilders);

        var jwks = jwksReader.readJWKMetadataFrom(properties.keySource(), properties.activeKid());
        registry.register(jwks);

        return registry;
    }

    @Bean
    SecureRandom secureRandom() {
        return CryptoServicesRegistrar.getSecureRandom();
    }

    @Bean
    JwtEncoder jwtEncoder(JWKSRegistry jwksRegistry, JWKSProperties properties) {
        var activeKid = properties.activeKid();
        var activeKey = jwksRegistry.get(activeKid);
        if (activeKey == null) throw new IllegalStateException("Active JWKS not found. kid=" + activeKid);

        JWKSource<SecurityContext> source = (selector, context) -> selector.select(new JWKSet(activeKey));

        return new NimbusJwtEncoder(source);
    }

    @Bean
    RandomDisplayName randomDisplayName(SecureRandom secureRandom, PolicyProperties properties) throws IOException {
        var props = properties.user();
        return new RandomDisplayName(secureRandom, props.displayNameAdjectiveSource(), props.displayNameNounSource());
    }

    @Bean
    OpaqueTokenHasher opaqueTokenHasher(ApplicationProperties properties) {
        return new HmacSha256paqueTokenHasher(properties.hasherKey().getBytes(StandardCharsets.UTF_8));
    }
}
