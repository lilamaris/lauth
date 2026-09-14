package com.lilamaris.lauth.identity.jdbc.support;

import com.lilamaris.lauth.identity.application.model.session.RefreshTokenContext;
import com.lilamaris.lauth.identity.application.port.out.RefreshTokenContextReader;
import com.lilamaris.lauth.identity.jdbc.RefreshTokenJdbcAdapter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@TestConfiguration(proxyBeanMethods = false)
public class RefreshSessionConcurrencyTestConfiguration {
    @Bean
    @Primary
    RefreshTokenContextReader refreshTokenContextReader(RefreshTokenJdbcAdapter delegate) {
        var barrier = new CyclicBarrier(2);

        return new RefreshTokenContextReader() {
            @Override
            public Optional<RefreshTokenContext> findByRefreshTokenId(UUID refreshTokenId) {
                var context = delegate.findByRefreshTokenId(refreshTokenId);
                try {
                    barrier.await(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrupted waiting", e);
                } catch (BrokenBarrierException | TimeoutException e) {
                    throw new IllegalStateException("Both transaction must reach the read context boundary.");
                }

                return context;
            }
        };
    }
}
