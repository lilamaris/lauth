package com.lilamaris.lauth.identity.jdbc;

import com.lilamaris.lauth.identity.application.exception.IdentityServiceProgressCode;
import com.lilamaris.lauth.identity.application.model.jwt.TokenPair;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenCodec;
import com.lilamaris.lauth.identity.application.port.in.command.RefreshSessionCommand;
import com.lilamaris.lauth.identity.application.service.RefreshSessionService;
import com.lilamaris.lauth.identity.jdbc.assertion.SessionAssertion;
import com.lilamaris.lauth.identity.jdbc.support.RefreshSessionConcurrencyTestConfiguration;
import com.lilamaris.lauth.identity.jdbc.support.RefreshSessionTestConfiguration;
import com.lilamaris.lauth.identity.jdbc.support.SessionTestSupport;
import com.lilamaris.lauth.identity.jdbc.support.UserTestSupport;
import com.lilamaris.lauth.kernel.application.exception.ApplicationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.show-sql=false",
        "spring.flyway.enabled=true"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
@Import({RefreshSessionTestConfiguration.class, RefreshSessionConcurrencyTestConfiguration.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Session Refresh 동시성 테스트")
public class RefreshSessionConcurrencyTest {
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18.3-alpine");
    private static final Instant NOW = RefreshSessionTestConfiguration.NOW;
    private UUID userId;

    @Autowired
    private JdbcClient jdbcClient;

    @Autowired
    private RefreshSessionService refreshSessionService;

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        POSTGRES.start();
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @BeforeAll
    void setupUserContext() {
        var context = UserTestSupport.createContext(jdbcClient, NOW);
        userId = context.userId();
    }

    @AfterAll
    void cleanUp() {
        UserTestSupport.cleanup(jdbcClient);
        POSTGRES.stop();
    }

    @AfterEach
    void cleanUpSessions() {
        SessionTestSupport.cleanup(jdbcClient, userId);
    }

    @Test
    @DisplayName("같은 유효 Refresh Token으로 동시에 세션을 갱신하면 해당 세션만 폐기된다")
    void only_reused_token_session_is_revoked() throws Exception {
        var context = SessionTestSupport.createContext(jdbcClient, userId, NOW);
        var token = OpaqueTokenCodec.encode(context.opaqueToken());
        var command = RefreshSessionCommand.of(token);

        var executor = Executors.newFixedThreadPool(2);

        List<Outcome> outcomes;
        try {
            // 서로 다른 트랜잭션의 두 스레드가 동시에 동일한 토큰으로 세션 연장 시도
            // Cyclic barrier는 두 스레드가 consumedAt == null인 토큰을 읽은 시점에서 다른 스레드를 기다리도록 대기
            // 이를 통해 두 tx 모두 consumedAt이 null인 유효 토큰으로 판단하고 토큰 consume의 조건부 갱신까지 넘어갈 수 있음
            // 테스트에서 보려는 것은 조건부 갱신 시 승자는 consume 성공, 패자는 TOKEN_REUSE_DETECTED 분기로 넘어가서 해당 세션을 폐기 처리하는지
            var first = executor.submit(() -> run(command));
            var second = executor.submit(() -> run(command));
            outcomes = List.of(
                    first.get(30, TimeUnit.SECONDS),
                    second.get(30, TimeUnit.SECONDS)
            );
        } finally {
            executor.shutdownNow();
            assertThat(executor.awaitTermination(15, TimeUnit.SECONDS))
                    .as("workers must terminate")
                    .isTrue();
        }

        var results = outcomes.stream().collect(Collectors.partitioningBy(outcome -> outcome.result() != null));
        var successes = results.get(true);
        var failures = results.get(false);

        assertThat(successes).hasSize(1);
        assertThat(failures).hasSize(1);
        assertThat(failures.getFirst().exception())
                .extracting("applicationCode")
                .isEqualTo(IdentityServiceProgressCode.TOKEN_REUSE_DETECTED);

        SessionAssertion.assertConsumedAt(jdbcClient, context.refreshTokenId(), NOW);

        SessionAssertion.assertRevokedAt(jdbcClient, context.targetSessionId(), NOW);

        SessionAssertion.assertNotRevoked(jdbcClient, context.secondSessionId());
    }

    private Outcome run(RefreshSessionCommand command) {
        try {
            return new Outcome(refreshSessionService.refresh(command), null);
        } catch (ApplicationException e) {
            return new Outcome(null, e);
        }
    }

    private record Outcome(TokenPair result, Throwable exception) {
    }

    @Configuration(proxyBeanMethods = false)
    static class TestContext {
    }
}
