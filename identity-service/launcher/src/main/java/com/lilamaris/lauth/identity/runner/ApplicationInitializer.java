package com.lilamaris.lauth.identity.runner;

import com.lilamaris.lauth.identity.application.config.ScopeInitializer;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@NullMarked
@Component
@Order(0)
@RequiredArgsConstructor
public class ApplicationInitializer implements ApplicationRunner {
    private final ScopeInitializer scopeInitializer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        scopeInitializer.run(args);
    }
}
