package com.lilamaris.lauth.identity.security.handler;

import com.lilamaris.lauth.kenel.web.response.ServletResponseWriter;
import com.lilamaris.lauth.kenel.web.response.error.ProblemDetailFactory;
import com.lilamaris.lauth.kenel.web.response.error.StandardErrorDescriptor;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@NullMarked
@RequiredArgsConstructor
public class GlobalAccessDeniedHandler implements AccessDeniedHandler {
    private final ServletResponseWriter responseWriter;
    private final ProblemDetailFactory problemDetailFactory;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        var problem = problemDetailFactory.from(StandardErrorDescriptor.ACCESS_DENIED);
        responseWriter.write(response, problem);
    }
}
