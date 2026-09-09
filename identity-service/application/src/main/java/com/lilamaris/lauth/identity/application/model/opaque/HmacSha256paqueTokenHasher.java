package com.lilamaris.lauth.identity.application.model.opaque;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class HmacSha256paqueTokenHasher implements OpaqueTokenHasher {
    private static final String ALGO = "HmacSHA256";
    private final SecretKeySpec key;
    private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    private final Base64.Decoder decoder = Base64.getUrlDecoder();

    public HmacSha256paqueTokenHasher(byte[] secret) {
        if (secret == null || secret.length < 32) {
            throw new IllegalArgumentException("HMAC key must be at least 256 bits");
        }
        this.key = new SecretKeySpec(secret.clone(), ALGO);
    }

    @Override
    public String hash(OpaqueTokenPurpose purpose, String token) {
        byte[] digest = digest(purpose.getCanonicalName(), token);
        return encoder.encodeToString(digest);
    }

    @Override
    public boolean matches(OpaqueTokenPurpose purpose, String token, String expectedHash) {
        byte[] actual = digest(purpose.getCanonicalName(), token);
        byte[] expected;

        try {
            expected = decoder.decode(expectedHash);
            return MessageDigest.isEqual(actual, expected);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private byte[] digest(String purpose, String token) {
        try {
            Mac mac = Mac.getInstance(ALGO);
            mac.init(key);

            mac.update(purpose.getBytes(StandardCharsets.UTF_8));
            mac.update((byte) 0);
            mac.update(token.getBytes(StandardCharsets.UTF_8));

            return mac.doFinal();
        } catch (Exception e) {
            throw new IllegalStateException("HMAC calculation failed", e);
        }
    }
}
