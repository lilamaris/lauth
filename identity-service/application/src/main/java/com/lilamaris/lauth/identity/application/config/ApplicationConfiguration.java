package com.lilamaris.lauth.identity.application.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ApplicationProperties.class, JwtProperties.class})
public class ApplicationConfiguration {
}
