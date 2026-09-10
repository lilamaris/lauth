package com.lilamaris.lauth.identity.security.handler;

import com.lilamaris.lauth.identity.application.model.jwt.TokenPair;
import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.in.LoginSessionUseCase;
import com.lilamaris.lauth.kenel.web.response.ServletResponseWriter;
import com.lilamaris.lauth.kenel.web.response.error.ProblemDetailFactory;
import com.lilamaris.lauth.kenel.web.response.error.StandardErrorDescriptor;
import com.lilamaris.lauth.kernel.application.exception.ApplicationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@NullMarked
@Component
@RequiredArgsConstructor
public class GlobalAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final LoginSessionUseCase loginSessionUseCase;
    private final ServletResponseWriter responseWriter;
    private final ProblemDetailFactory problemDetailFactory;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (!(authentication.getPrincipal() instanceof UserPrincipal principal)) return;

        ProblemDetail problem = null;
        TokenPair tokenPair = null;

        try {
            tokenPair = loginSessionUseCase.login(principal);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            if (e instanceof ApplicationException applicationException) {
                problem = problemDetailFactory.from(applicationException.getApplicationCode());
            } else {
                problem = problemDetailFactory.from(StandardErrorDescriptor.INTERNAL_SERVER_ERROR);
            }
        }

        if (tokenPair != null) responseWriter.write(response, HttpStatus.OK, tokenPair);
        else if (problem != null) responseWriter.write(response, problem);

    }
}
