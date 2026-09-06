package com.lilamaris.lauth.identity.runner;

import com.lilamaris.lauth.identity.application.config.ScopeInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationInitializer implements ApplicationRunner {
    private final ScopeInitializer scopeInitializer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        scopeInitializer.run(args);
    }
}
