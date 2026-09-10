package com.lilamaris.lauth.identity.web.config;

import com.lilamaris.lauth.identity.application.exception.IdentityServiceProgressType;
import com.lilamaris.lauth.kenel.web.response.error.*;
import com.lilamaris.shrturl.kernel.application.exception.StandardProgressType;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.List;

@Configuration
@EnableConfigurationProperties(WebProperties.class)
public class WebConfiguration {
    @Bean
    ErrorStatusRegistry errorStatusRegistry(List<ErrorStatusRegistrar> registrars) {
        return new ErrorStatusRegistry(registrars);
    }

    @Bean
    ErrorStatusRegistrar webErrorStatusRegistrar() {
        return new ErrorStatusRegistrar()
                .type(IdentityServiceProgressType.AUTHENTICATION_FAILED).mapsTo(HttpStatus.UNAUTHORIZED)
                .type(IdentityServiceProgressType.GRANT_FAILED).mapsTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .type(IdentityServiceProgressType.RESOURCE_EXPIRED).mapsTo(HttpStatus.UNAUTHORIZED)
                .type(IdentityServiceProgressType.TOKEN_VERIFICATION_FAILED).mapsTo(HttpStatus.UNAUTHORIZED);
    }

    @Bean
    ErrorStatusRegistrar defaultErrorStatusRegistrar() {
        return new ErrorStatusRegistrar()
                .type(StandardProgressType.BAD_REQUEST).mapsTo(HttpStatus.BAD_REQUEST)
                .type(StandardProgressType.NOT_FOUND).mapsTo(HttpStatus.NOT_FOUND)
                .type(StandardProgressType.DUPLICATED).mapsTo(HttpStatus.CONFLICT)
                .type(StandardProgressType.ACCESS_DENIED).mapsTo(HttpStatus.FORBIDDEN)
                .type(StandardProgressType.UNAUTHORIZED).mapsTo(HttpStatus.UNAUTHORIZED)
                .type(StandardProgressType.FAILED).mapsTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Bean
    DefaultErrorCodeResolver defaultErrorCodeResolver() {
        return new DefaultErrorCodeResolver();
    }

    @Bean
    DefaultTypeUriResolver defaultTypeUriResolver(WebProperties properties) {
        return new DefaultTypeUriResolver(properties.baseUrl());
    }

    @Bean
    ProblemDetailFactory problemDetailFactory(ErrorCodeResolver errorCodeResolver, TypeUriResolver typeUriResolver, ErrorStatusRegistry errorStatusRegistry) {
        return new ProblemDetailFactory(errorCodeResolver, typeUriResolver, errorStatusRegistry);
    }
}
