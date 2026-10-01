package com.lilamaris.lauth.identity.runner;

import com.lilamaris.lauth.identity.application.config.TestUserIds;
import com.lilamaris.lauth.identity.application.config.TestUserProperties;
import com.lilamaris.lauth.identity.application.model.scope.ResourceScope;
import com.lilamaris.lauth.identity.application.model.scope.ScopeCodec;
import com.lilamaris.lauth.identity.application.port.out.ScopeReader;
import com.lilamaris.lauth.identity.application.port.out.UserGrantStore;
import com.lilamaris.lauth.identity.application.port.out.UserStore;
import com.lilamaris.lauth.identity.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Clock;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Order(1)
@ConditionalOnProperty(prefix = "lauth.test", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class TestUserSeeder implements ApplicationRunner {
    private final UserStore userStore;
    private final UserGrantStore userGrantStore;
    private final ScopeReader scopeReader;
    private final TestUserIds testUserIds;
    private final TestUserProperties properties;
    private final Clock clock;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var now = clock.instant();
        var ids = testUserIds.ids();
        var scopeIds = scopeReader.findAll().stream()
                .filter(scope -> Set.of("user.read", "user.write")
                        .contains(ScopeCodec.encode(ResourceScope.from(scope))))
                .map(scope -> scope.getId())
                .collect(Collectors.toSet());
        if (scopeIds.size() != 2) throw new IllegalStateException("Test mode requires user.read and user.write scopes");
        for (int index = 0; index < ids.size(); index++) {
            userStore.saveIfAbsent(User.of(ids.get(index), "k6-test-user-" + (index + 1), now));
            userGrantStore.grantIfAbsent(ids.get(index), scopeIds, now);
        }

        var target = properties.idsFile().toAbsolutePath();
        var parent = target.getParent();
        Files.createDirectories(parent);
        var temporary = Files.createTempFile(parent, ".lauth-test-user-ids-", ".tmp");
        try {
            Files.write(temporary, ids.stream().map(String::valueOf).toList());
            try {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
        log.info("Seeded {} k6 test users and wrote IDs to {}", ids.size(), target);
    }
}
