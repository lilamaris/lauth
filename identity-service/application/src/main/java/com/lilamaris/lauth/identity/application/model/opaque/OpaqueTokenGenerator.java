package com.lilamaris.lauth.identity.application.model.opaque;

import com.lilamaris.lauth.identity.application.internal.random.RandomGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class OpaqueTokenGenerator {
    private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    private final SecureRandom secureRandom;

    public String generate() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return encoder.encodeToString(bytes);
    }
}
