package com.lilamaris.lauth.identity.security.method.credential.request;

import com.lilamaris.lauth.identity.security.exception.AuthenticationProcessingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@NullMarked
public class JacksonSignInProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper objectMapper;
    @Getter
    private final RequestMatcher requestMatcher;

    public JacksonSignInProcessingFilter(RequestMatcher requestMatcher, ObjectMapper objectMapper) {
        super(requestMatcher);
        this.requestMatcher = requestMatcher;
        this.objectMapper = objectMapper;
    }

    @Override
    public @Nullable Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        try {
            var body = objectMapper.readValue(request.getInputStream(), SignInRequest.class);
            var auth = CredentialAuthenticateToken.of(body.toCredential());
            return getAuthenticationManager().authenticate(auth);
        } catch (Exception e) {
            throw new AuthenticationProcessingException("Invalid credential sign-in request.", e);
        }
    }
}