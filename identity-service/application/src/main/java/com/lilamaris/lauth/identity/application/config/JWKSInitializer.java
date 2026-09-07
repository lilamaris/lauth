package com.lilamaris.lauth.identity.application.config;

import com.lilamaris.lauth.identity.application.internal.jwks.JWKSRegistry;
import com.lilamaris.lauth.identity.application.port.out.JWKSReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWKSInitializer {
    private final JWKSReader reader;
    private final JWKSProperties properties;
    private final JWKSRegistry jwksRegistry;

    public void run(ApplicationArguments args) {
        var jwks = reader.readJWKMetadataFrom(properties.keySource(), properties.activeKid());
        log.info("{} JWK Founded.", jwks.size());

        jwksRegistry.register(jwks);
    }
}
