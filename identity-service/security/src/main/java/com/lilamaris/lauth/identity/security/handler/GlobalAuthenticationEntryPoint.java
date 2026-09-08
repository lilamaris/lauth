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
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@NullMarked
@RequiredArgsConstructor
public class GlobalAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ServletResponseWriter responseWriter;
    private final ProblemDetailFactory problemDetailFactory;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        var problem = problemDetailFactory.from(StandardErrorDescriptor.UNAUTHORIZED);
        responseWriter.write(response, problem);
    }
}
