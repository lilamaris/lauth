package com.lilamaris.lauth.identity.jdbc;

import com.lilamaris.lauth.identity.application.exception.IdentityServiceProgressCode;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenCodec;
import com.lilamaris.lauth.identity.application.port.in.command.RefreshSessionCommand;
import com.lilamaris.lauth.identity.application.service.RefreshSessionService;
import com.lilamaris.lauth.identity.jdbc.assertion.SessionAssertion;
import com.lilamaris.lauth.identity.jdbc.support.RefreshSessionTestConfiguration;
import com.lilamaris.lauth.identity.jdbc.support.SessionTestSupport;
import com.lilamaris.lauth.identity.jdbc.support.UserTestSupport;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.show-sql=false",
        "spring.flyway.enabled=true"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
@Import(RefreshSessionTestConfiguration.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Refresh Token ")
public class RefreshSessionRotationTest {
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18.3-alpine");
    private static final Instant NOW = RefreshSessionTestConfiguration.NOW;
    private UUID userId;

    @Autowired
    private JdbcClient jdbcClient;

    @Autowired
    private RefreshSessionService refreshSessionService;

    @Autowired
    private SessionJdbcAdapter sessionJdbcAdapter;

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
    @DisplayName("세션 갱신에 사용된 Refresh Token은 사용 처리되고, 다음 토큰을 발급한다.")
    void consume_token_and_issue_next() {
        var context = SessionTestSupport.createContext(jdbcClient, userId, NOW);
        var token = OpaqueTokenCodec.encode(context.opaqueToken());
        var command = RefreshSessionCommand.of(token);

        var result = refreshSessionService.refresh(command);

        SessionAssertion.assertConsumedAt(jdbcClient, context.refreshTokenId(), NOW);

        var nextToken = result.refreshToken().value();
        var nextOpaqueToken = OpaqueTokenCodec.decode(nextToken);
        var nextRefreshTokenId = UUID.fromString(nextOpaqueToken.selector());

        SessionAssertion.assertNotConsumed(jdbcClient, nextRefreshTokenId);
    }

    @Test
    @DisplayName("이미 사용한 Refresh Token을 재사용하면 해당 세션이 폐기된다")
    void revoke_session_when_detect_token_reuse() {
        var context = SessionTestSupport.createContext(jdbcClient, userId, NOW);
        var token = OpaqueTokenCodec.encode(context.opaqueToken());
        var command = RefreshSessionCommand.of(token);

        var result = refreshSessionService.refresh(command);
        var nextToken = result.refreshToken().value();

        assertThatThrownBy(() -> refreshSessionService.refresh(command))
                .extracting("applicationCode")
                .isEqualTo(IdentityServiceProgressCode.TOKEN_REUSE_DETECTED);

        SessionAssertion.assertRevokedAt(jdbcClient, context.targetSessionId(), NOW);

        SessionAssertion.assertNotRevoked(jdbcClient, context.secondSessionId());

        var nextCommand = RefreshSessionCommand.of(nextToken);
        assertThatThrownBy(() -> refreshSessionService.refresh(nextCommand))
                .extracting("applicationCode")
                .isEqualTo(IdentityServiceProgressCode.INVALID_SESSION);
    }

    @Test
    @DisplayName("세션이 유효할 때 갱신한 토큰이어도, 세션이 폐기된 후에는 갱신할 수 없다.")
    void reject_refresh_when_session_revoked() {
        var context = SessionTestSupport.createContext(jdbcClient, userId, NOW);
        var token = OpaqueTokenCodec.encode(context.opaqueToken());
        var command = RefreshSessionCommand.of(token);

        var result = refreshSessionService.refresh(command);
        var nextToken = result.refreshToken().value();
        var nextOpaqueToken = OpaqueTokenCodec.decode(nextToken);
        var nextRefreshTokenId = UUID.fromString(nextOpaqueToken.selector());

        SessionAssertion.assertConsumedAt(jdbcClient, context.refreshTokenId(), NOW);
        SessionAssertion.assertNotRevoked(jdbcClient, context.targetSessionId());

        sessionJdbcAdapter.tryRevoke(context.targetSessionId(), NOW);

        SessionAssertion.assertRevokedAt(jdbcClient, context.targetSessionId(), NOW);

        var nextCommand = RefreshSessionCommand.of(nextToken);
        assertThatThrownBy(() -> refreshSessionService.refresh(nextCommand))
                .extracting("applicationCode")
                .isEqualTo(IdentityServiceProgressCode.INVALID_SESSION);

        SessionAssertion.assertNotConsumed(jdbcClient, nextRefreshTokenId);
    }

    @Configuration(proxyBeanMethods = false)
    static class TestContext {
    }
}
