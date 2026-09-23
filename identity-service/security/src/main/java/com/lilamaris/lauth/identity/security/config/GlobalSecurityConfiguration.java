package com.lilamaris.lauth.identity.security.config;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.security.handler.GlobalAccessDeniedHandler;
import com.lilamaris.lauth.identity.security.handler.GlobalAuthenticationEntryPoint;
import com.lilamaris.lauth.identity.security.handler.GlobalAuthenticationFailureHandler;
import com.lilamaris.lauth.identity.security.method.credential.provider.CredentialSignInProvider;
import com.lilamaris.lauth.identity.security.method.credential.request.JacksonSignInProcessingFilter;
import com.lilamaris.lauth.identity.security.method.federated.service.CustomOAuth2UserService;
import com.lilamaris.lauth.identity.security.method.federated.service.CustomOidcUserService;
import com.lilamaris.lauth.identity.security.principal.CurrentUserPrincipal;
import com.lilamaris.lauth.kenel.web.response.ServletResponseWriter;
import com.lilamaris.lauth.kenel.web.response.error.ProblemDetailFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
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
    @Order(2)
    SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            SecurityContextRepository securityContextRepository,
            UrlBasedCorsConfigurationSource corsConfigurationSource,
            GlobalAccessDeniedHandler globalAccessDeniedHandler,
            GlobalAuthenticationEntryPoint globalAuthenticationEntryPoint,
            GlobalAuthenticationFailureHandler globalAuthenticationFailureHandler,
            CustomOAuth2UserService customOAuth2UserService,
            CustomOidcUserService customOidcUserService,
            JacksonSignInProcessingFilter jacksonSignInProcessingFilter,
            JwtDecoder jwtDecoder,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            GlobalSecurityProperties properties
    ) {
        if (properties.csrfEnabled()) httpSecurity.csrf(Customizer.withDefaults());
        else httpSecurity.csrf(AbstractHttpConfigurer::disable);

        httpSecurity

                .cors(customizer -> customizer.configurationSource(corsConfigurationSource))

                .securityContext(customizer -> customizer.securityContextRepository(securityContextRepository))
                .sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
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
                        .requestMatchers("/api/v1/user/**").authenticated()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .anyRequest().denyAll()
                )

                .oauth2Login(customizer -> customizer
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .oidcUserService(customOidcUserService)
                                .userService(customOAuth2UserService)
                        )
                        .failureHandler(globalAuthenticationFailureHandler)
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
    RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder) {
        RegisteredClient oidcClient = RegisteredClient.withId("oidc-client")
                .clientId("oidc-client")
                .clientSecret(passwordEncoder.encode("secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("https://app.apidog.com/oauth2-browser-callback.html")
                .redirectUri("http://localhost:5174/auth/callback")
                .postLogoutRedirectUri("http://127.0.0.1:8080/")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("user.read")
                .scope("user.write")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();

        return new InMemoryRegisteredClientRepository(oidcClient);
    }

    @Bean
    AuthenticationManager authenticationManager(CredentialSignInProvider credentialSignInProvider) {
        return new ProviderManager(credentialSignInProvider);
    }

    @Bean
    SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
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
