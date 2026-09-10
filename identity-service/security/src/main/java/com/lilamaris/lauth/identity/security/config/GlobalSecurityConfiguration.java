package com.lilamaris.lauth.identity.security.config;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.in.LoginSessionUseCase;
import com.lilamaris.lauth.identity.security.handler.GlobalAccessDeniedHandler;
import com.lilamaris.lauth.identity.security.handler.GlobalAuthenticationEntryPoint;
import com.lilamaris.lauth.identity.security.handler.GlobalAuthenticationFailureHandler;
import com.lilamaris.lauth.identity.security.handler.GlobalAuthenticationSuccessHandler;
import com.lilamaris.lauth.identity.security.method.credential.provider.CredentialSignInProvider;
import com.lilamaris.lauth.identity.security.method.credential.request.JacksonSignInProcessingFilter;
import com.lilamaris.lauth.identity.security.principal.CurrentUserPrincipal;
import com.lilamaris.lauth.kenel.web.response.ServletResponseWriter;
import com.lilamaris.lauth.kenel.web.response.error.ProblemDetailFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;
import java.util.UUID;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties({GlobalSecurityProperties.class, GlobalCorsProperties.class})
public class GlobalSecurityConfiguration {

    @Bean
    AuthenticationManager authenticationManager(CredentialSignInProvider credentialSignInProvider) {
        return new ProviderManager(credentialSignInProvider);
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            UrlBasedCorsConfigurationSource corsConfigurationSource,
            GlobalAccessDeniedHandler globalAccessDeniedHandler,
            GlobalAuthenticationEntryPoint globalAuthenticationEntryPoint,
            JacksonSignInProcessingFilter jacksonSignInProcessingFilter,
            JwtDecoder jwtDecoder,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            GlobalSecurityProperties properties
    ) {
        if (properties.csrfEnabled()) httpSecurity.csrf(Customizer.withDefaults());
        else httpSecurity.csrf(AbstractHttpConfigurer::disable);

        httpSecurity

                .cors(customizer -> customizer.configurationSource(corsConfigurationSource))

                .sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                .exceptionHandling(customizer -> customizer
                        .accessDeniedHandler(globalAccessDeniedHandler)
                        .authenticationEntryPoint(globalAuthenticationEntryPoint)
                )

                .authorizeHttpRequests(customizer -> customizer
                        .requestMatchers(properties.permits().toArray(String[]::new)).permitAll()
                        .requestMatchers(jacksonSignInProcessingFilter.getRequestMatcher()).permitAll()
                        .requestMatchers("/api/v1/session/**").authenticated()
                        .anyRequest().denyAll()
                )

                .oauth2ResourceServer(customizer -> customizer
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                        .authenticationEntryPoint(globalAuthenticationEntryPoint)
                )

                .addFilterBefore(jacksonSignInProcessingFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource(GlobalCorsProperties properties) {
        var configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(properties.allowedOrigins());
        configuration.setAllowedMethods(properties.allowedMethods());
        configuration.setAllowedHeaders(properties.allowedHeaders());
        configuration.setAllowCredentials(properties.allowCredentials());
        configuration.setExposedHeaders(properties.exposedHeaders());
        configuration.validateAllowCredentials();

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();

        converter.setJwtPrincipalConverter(jwt -> {
            var user = UserPrincipal.of(
                    UUID.fromString(Objects.requireNonNull(jwt.getSubject())),
                    jwt.getClaimAsString("display"),
                    jwt.getClaimAsInstant("createdAt"),
                    jwt.getClaimAsInstant("updatedAt")
            );
            return new CurrentUserPrincipal(user, jwt.getClaims());
        });

        return converter;
    }

    @Bean
    GlobalAuthenticationSuccessHandler globalAuthenticationSuccessHandler(LoginSessionUseCase loginSessionUseCase, com.lilamaris.lauth.kenel.web.response.ServletResponseWriter servletResponseWriter, ProblemDetailFactory problemDetailFactory) {
        return new GlobalAuthenticationSuccessHandler(loginSessionUseCase, servletResponseWriter, problemDetailFactory);
    }

    @Bean
    GlobalAuthenticationFailureHandler globalAuthenticationFailureHandler(ServletResponseWriter servletResponseWriter, ProblemDetailFactory problemDetailFactory) {
        return new GlobalAuthenticationFailureHandler(servletResponseWriter, problemDetailFactory);
    }

    @Bean
    GlobalAccessDeniedHandler globalAccessDeniedHandler(ServletResponseWriter servletResponseWriter, ProblemDetailFactory problemDetailFactory) {
        return new GlobalAccessDeniedHandler(servletResponseWriter, problemDetailFactory);
    }

    @Bean
    GlobalAuthenticationEntryPoint globalAuthenticationEntryPoint(ServletResponseWriter servletResponseWriter, ProblemDetailFactory problemDetailFactory) {
        return new GlobalAuthenticationEntryPoint(servletResponseWriter, problemDetailFactory);
    }

    @Bean
    ServletResponseWriter servletResponseWriter(ObjectMapper objectMapper) {
        return new ServletResponseWriter(objectMapper);
    }
}
