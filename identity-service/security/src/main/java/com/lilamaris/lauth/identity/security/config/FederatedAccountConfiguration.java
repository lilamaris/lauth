package com.lilamaris.lauth.identity.security.config;

import com.lilamaris.lauth.identity.application.port.in.ResolveFederatedAccountUseCase;
import com.lilamaris.lauth.identity.security.method.federated.resolver.FederatedIdentityResolverRegistry;
import com.lilamaris.lauth.identity.security.method.federated.service.CustomOAuth2UserService;
import com.lilamaris.lauth.identity.security.method.federated.service.CustomOidcUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FederatedAccountConfiguration {
    @Bean
    CustomOAuth2UserService customOAuth2UserService(ResolveFederatedAccountUseCase resolveFederatedAccountUseCase, FederatedIdentityResolverRegistry resolverRegistry) {
        return new CustomOAuth2UserService(resolveFederatedAccountUseCase, resolverRegistry);
    }

    @Bean
    CustomOidcUserService customOidcUserService(ResolveFederatedAccountUseCase resolveFederatedAccountUseCase, FederatedIdentityResolverRegistry resolverRegistry) {
        return new CustomOidcUserService(resolveFederatedAccountUseCase, resolverRegistry);
    }
}
