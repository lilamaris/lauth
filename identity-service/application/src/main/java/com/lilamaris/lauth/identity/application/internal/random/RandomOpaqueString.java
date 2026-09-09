package com.lilamaris.lauth.identity.application.internal.random;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class RandomOpaqueString implements RandomGenerator<String> {
    private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    private final SecureRandom secureRandom;

    @Override
    public String generate() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return encoder.encodeToString(bytes);
    }
}
