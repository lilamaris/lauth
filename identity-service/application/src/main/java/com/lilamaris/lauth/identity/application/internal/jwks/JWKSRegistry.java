package com.lilamaris.lauth.identity.application.internal.jwks;

import com.lilamaris.lauth.identity.application.model.jwks.JWKMetadata;
import com.nimbusds.jose.jwk.JWK;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class JWKSRegistry {
    private final Map<JWKKey, JWK> jwkRegistry = new HashMap<>();
    private final Map<JWKBuilderKey, JWKBuilder<?>> jwkBuilderRegistry;

    public JWKSRegistry(List<JWKBuilder<?>> jwkBuilders) {
        Map<JWKBuilderKey, JWKBuilder<?>> jwkBuilderRegistry = new HashMap<>();

        jwkBuilders.forEach(jwkBuilder -> {
            var key = JWKBuilderKey.of(jwkBuilder);

            if (jwkBuilderRegistry.putIfAbsent(key, jwkBuilder) != null)
                throw new IllegalArgumentException("Duplicated JWK Builder founded. key=" + key);
        });

        this.jwkBuilderRegistry = Map.copyOf(jwkBuilderRegistry);
    }

    public JWK get(String kid) {
        var key = JWKKey.of(kid);
        return jwkRegistry.get(key);
    }

    public List<JWK> getAll() {
        return jwkRegistry.values().stream().toList();
    }

    public void register(JWKMetadata metadata) {
        var key = JWKKey.of(metadata);
        if (jwkRegistry.containsKey(key))
            throw new IllegalArgumentException("Duplicated JWK founded. key=" + key);

        jwkRegistry.put(key, build(metadata));
    }

    public void register(Collection<JWKMetadata> metadata) {
        metadata.forEach(this::register);
    }

    @SuppressWarnings("unchecked")
    private <T extends JWK> T build(JWKMetadata metadata) {
        var key = JWKBuilderKey.of(metadata);
        var builder = jwkBuilderRegistry.get(key);
        if (builder == null)
            throw new IllegalArgumentException("JWK Builder not founded. key=" + key);

        try {
            var result = builder.build(metadata);
            return (T) builder.target().cast(result);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build JWK. kid=" + metadata.kid(), e);
        }
    }

    private record JWKKey(String kid) {
        public static JWKKey of(JWKMetadata metadata) {
            return new JWKKey(metadata.kid());
        }

        public static JWKKey of(String kid) {
            return new JWKKey(kid);
        }
    }

    private record JWKBuilderKey(String alg) {
        public static JWKBuilderKey of(JWKMetadata metadata) {
            return new JWKBuilderKey(metadata.alg().getName());
        }

        public static JWKBuilderKey of(JWKBuilder<?> jwkBuilder) {
            return new JWKBuilderKey(jwkBuilder.support().getName());
        }
    }
}
