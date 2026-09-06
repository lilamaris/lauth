package com.lilamaris.lauth.identity.application.config;

import com.lilamaris.lauth.identity.application.port.out.ScopeStore;
import com.lilamaris.lauth.identity.domain.scope.Scope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScopeInitializer {
    private final ScopeStore scopeStore;
    private final ScopeProperties properties;
    private final Clock clock;

    public void run(ApplicationArguments args) {
        if (!properties.sync()) {
            log.info("Scope definition sync is disabled.");
            return;
        }

        var now = clock.instant();
        var scopes = properties.definitions().stream()
                .map(definition -> Scope.of(
                        definition.resource(),
                        definition.action(),
                        definition.description(),
                        now
                ))
                .collect(Collectors.toUnmodifiableSet());

        var created = scopeStore.saveAll(scopes);

        if (!created) {
            log.error("Scope definition sync failed.");
        } else {
            log.info("Scope definition successfully synced.");
        }
    }
}
