package com.lilamaris.lauth.identity.jdbc.support;

import com.lilamaris.lauth.identity.application.config.session.RefreshTokenProperties;
import com.lilamaris.lauth.identity.application.internal.id.IdGenerator;
import com.lilamaris.lauth.identity.application.internal.session.AccessTokenService;
import com.lilamaris.lauth.identity.application.internal.session.RefreshTokenService;
import com.lilamaris.lauth.identity.application.internal.session.SessionTxService;
import com.lilamaris.lauth.identity.application.model.jwt.TokenMetadata;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenGenerator;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenHasher;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenPurpose;
import com.lilamaris.lauth.identity.application.service.RefreshSessionService;
import com.lilamaris.lauth.identity.domain.RefreshToken;
import com.lilamaris.lauth.identity.jdbc.RefreshTokenJdbcAdapter;
import com.lilamaris.lauth.identity.jdbc.SessionJdbcAdapter;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration(proxyBeanMethods = false)
@AutoConfigurationPackage(basePackageClasses = RefreshTokenJdbcAdapter.class)
@EntityScan(basePackageClasses = RefreshToken.class)
@Import({RefreshSessionService.class, SessionTxService.class, RefreshTokenJdbcAdapter.class,
        RefreshTokenService.class, SessionJdbcAdapter.class})
public class RefreshSessionTestConfiguration {
    public static final Instant NOW = Instant.parse("2026-09-12T00:00:00Z");

    @Bean
    RefreshTokenProperties refreshTokenProperties() {
        return new RefreshTokenProperties(Duration.ofDays(7));
    }

    @Bean
    AccessTokenService accessTokenService() {
        var service = mock(AccessTokenService.class);
        when(service.issue(any(UUID.class), any(UUID.class), any(Instant.class)))
                .thenAnswer(invocation -> {
                    Instant issuedAt = invocation.getArgument(2);
                    return TokenMetadata.accessToken("access-token", issuedAt, issuedAt.plusSeconds(300));
                });
        return service;
    }

    @Bean
    OpaqueTokenHasher opaqueTokenHasher() {
        return new OpaqueTokenHasher() {
            @Override
            public String hash(OpaqueTokenPurpose purpose, String token) {
                return "hash:" + token;
            }

            @Override
            public boolean matches(OpaqueTokenPurpose purpose, String token, String expectedHash) {
                return hash(purpose, token).equals(expectedHash);
            }
        };
    }

    @Bean
    OpaqueTokenGenerator opaqueTokenGenerator() {
        return new OpaqueTokenGenerator(new SecureRandom());
    }

    @Bean
    IdGenerator<UUID> idGenerator() {
        return UUID::randomUUID;
    }

    @Bean
    Clock clock() {
        return Clock.fixed(NOW, ZoneOffset.UTC);
    }
}
