package com.lilamaris.lauth.identity.security.config;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.in.LoginSessionUseCase;
import com.lilamaris.lauth.identity.security.handler.GlobalAccessDeniedHandler;
import com.lilamaris.lauth.identity.security.handler.GlobalAuthenticationEntryPoint;
import com.lilamaris.lauth.identity.security.handler.GlobalAuthenticationFailureHandler;
import com.lilamaris.lauth.identity.security.handler.GlobalAuthenticationSuccessHandler;
import com.lilamaris.lauth.identity.security.method.credential.provider.CredentialSignInProvider;
import com.lilamaris.lauth.identity.security.method.credential.request.JacksonSignInProcessingFilter;
import com.lilamaris.lauth.identity.security.method.federated.resolver.FederatedUserPrincipal;
import com.lilamaris.lauth.identity.security.method.federated.service.CustomOAuth2UserService;
import com.lilamaris.lauth.identity.security.method.federated.service.CustomOidcUserService;
import com.lilamaris.lauth.identity.security.principal.CurrentUserPrincipal;
import com.lilamaris.lauth.kenel.web.response.ServletResponseWriter;
import com.lilamaris.lauth.kenel.web.response.error.ProblemDetailFactory;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
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
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;
import java.util.UUID;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
@EnableConfigurationProperties({GlobalSecurityProperties.class, GlobalCorsProperties.class})
public class GlobalSecurityConfiguration {
    @Bean
    JWKSource<SecurityContext> jwkSource(JWK activeJWK) {
        return (jwkSelector, context) -> jwkSelector.select(new JWKSet(activeJWK));
    }

    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> jwtEncodingContextOAuth2TokenCustomizer() {
        return context -> {
            if (!OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) return;
            if (context.getPrincipal() == null) return;

            UserPrincipal principal = switch (context.getPrincipal().getPrincipal()) {
                case UserPrincipal user -> user;
                case FederatedUserPrincipal federated -> federated.user();
                case null, default -> null;
            };

            if (principal == null) return;

            var claims = context.getClaims();

            claims.claim("display", principal.displayName());
        };
    }

    @Bean
    @Order(1)
    SecurityFilterChain authorizationServerFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .oauth2AuthorizationServer(configurer -> {
                    httpSecurity.securityMatcher(configurer.getEndpointsMatcher());
                    configurer
                            .oidc(Customizer.withDefaults());
                })
                .authorizeHttpRequests(customizer ->
                        customizer
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                );

        return httpSecurity.build();
    }

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
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/oidc-client")
                // Apidog 공식 브라우저 콜백 — SAS 는 완전 일치 검증이라 Apidog UI 가 안내하는
                // URL 과 1:1 로 같아야 한다. (점: oauth2-browser.callback.html 은 Apidog SPA
                // fallback 으로 빠져 callback 을 못 받는 흔한 실수 — 하이픈이 정답)
                .redirectUri("https://app.apidog.com/oauth2-browser-callback.html")
                // Apidog 신규 문서의 공식 콜백도 함께 등록 (버전별 사용 URL 이 다를 수 있음)
                .redirectUri("https://oauth.apidog.com/v1/browser-callback")
                .postLogoutRedirectUri("http://127.0.0.1:8080/")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
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
