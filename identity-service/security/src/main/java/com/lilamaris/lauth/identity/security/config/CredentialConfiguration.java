package com.lilamaris.lauth.identity.security.config;

import com.lilamaris.lauth.identity.application.port.in.AuthenticateCredentialUseCase;
import com.lilamaris.lauth.identity.security.handler.GlobalAuthenticationFailureHandler;
import com.lilamaris.lauth.identity.security.method.credential.provider.CredentialSignInProvider;
import com.lilamaris.lauth.identity.security.method.credential.request.JacksonSignInProcessingFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableConfigurationProperties(CredentialProperties.class)
public class CredentialConfiguration {
    @Bean
    JacksonSignInProcessingFilter jacksonSignInProcessingFilter(
            CredentialProperties properties,
            AuthenticationManager authenticationManager,
            GlobalAuthenticationFailureHandler failureHandler,
            ObjectMapper objectMapper
    ) {
        var matcher = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, properties.signInEndpoint());
        var filter = new JacksonSignInProcessingFilter(matcher, objectMapper);
        var securityContextRepository = new HttpSessionSecurityContextRepository();

        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationFailureHandler(failureHandler);
        filter.setSecurityContextRepository(securityContextRepository);
        filter.setSessionAuthenticationStrategy(new ChangeSessionIdAuthenticationStrategy());
        filter.setAuthenticationSuccessHandler(new SavedRequestAwareAuthenticationSuccessHandler());

        return filter;
    }

    @Bean
    CredentialSignInProvider credentialSignInProvider(AuthenticateCredentialUseCase authenticateCredentialUseCase) {
        return new CredentialSignInProvider(authenticateCredentialUseCase);
    }
}

