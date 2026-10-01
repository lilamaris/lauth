package com.lilamaris.lauth.identity.runner;

import com.lilamaris.lauth.identity.application.config.TestUserIds;
import com.lilamaris.lauth.identity.application.config.TestUserProperties;
import com.lilamaris.lauth.identity.application.port.out.ScopeReader;
import com.lilamaris.lauth.identity.application.port.out.UserGrantStore;
import com.lilamaris.lauth.identity.application.port.out.UserStore;
import com.lilamaris.lauth.identity.domain.User;
import com.lilamaris.lauth.identity.domain.scope.Action;
import com.lilamaris.lauth.identity.domain.scope.Scope;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TestUserSeederTest {
    @TempDir Path directory;

    @Test
    void writesStableIdsAfterSeedingUsersAndGrants() throws Exception {
        var properties = new TestUserProperties(true, 3, directory.resolve("shared/users.txt"), "secret");
        var ids = new TestUserIds(properties);
        var now = Instant.parse("2026-01-01T00:00:00Z");
        var userStore = mock(UserStore.class);
        var grantStore = mock(UserGrantStore.class);
        var read = mock(Scope.class);
        when(read.getResource()).thenReturn("user");
        when(read.getAction()).thenReturn(Action.READ);
        when(read.getId()).thenReturn(java.util.UUID.randomUUID());
        var write = mock(Scope.class);
        when(write.getResource()).thenReturn("user");
        when(write.getAction()).thenReturn(Action.WRITE);
        when(write.getId()).thenReturn(java.util.UUID.randomUUID());
        ScopeReader scopes = () -> Set.of(read, write);
        var seeder = new TestUserSeeder(userStore, grantStore, scopes, ids, properties,
                Clock.fixed(now, ZoneOffset.UTC));

        seeder.run(null);
        var lines = Files.readAllLines(properties.idsFile());
        assertThat(lines).containsExactlyElementsOf(ids.ids().stream().map(String::valueOf).toList());
        verify(userStore, times(3)).saveIfAbsent(any(User.class));
        verify(grantStore, times(3)).grantIfAbsent(any(), any(), any());

        seeder.run(null);
        assertThat(Files.readAllLines(properties.idsFile())).isEqualTo(lines);
    }
}
