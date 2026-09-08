package com.lilamaris.lauth.identity.security.handler;

import com.lilamaris.lauth.kenel.web.response.ServletResponseWriter;
import com.lilamaris.lauth.kenel.web.response.error.ProblemDetailFactory;
import com.lilamaris.lauth.kenel.web.response.error.StandardErrorDescriptor;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@NullMarked
@Component
@RequiredArgsConstructor
public class GlobalAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ServletResponseWriter responseWriter;
    private final ProblemDetailFactory problemDetailFactory;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        var problem = problemDetailFactory.from(StandardErrorDescriptor.UNAUTHORIZED);
        responseWriter.write(response, problem);
    }
}
