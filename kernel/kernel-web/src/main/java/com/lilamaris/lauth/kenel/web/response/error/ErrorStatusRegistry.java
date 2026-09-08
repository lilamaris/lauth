package com.lilamaris.lauth.kenel.web.response.error;

import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ErrorStatusRegistry {
    private final Map<String, HttpStatus> registry;

    public ErrorStatusRegistry(Collection<ErrorStatusRegistrar> registrars) {
        var registry = new HashMap<String, HttpStatus>();

        for (var registrar : registrars) {
            for (var mapping : registrar.mappings()) {
                var prev = registry.putIfAbsent(mapping.type(), mapping.status());

                if (prev != null) throw new IllegalStateException(
                        "Duplicated error status found. type=%s, previous=%s, current=%s"
                                .formatted(mapping.type(), prev, mapping.status())
                );
            }
        }

        this.registry = Map.copyOf(registry);
    }

    public HttpStatus get(String type) {
        return registry.get(type);
    }
}
