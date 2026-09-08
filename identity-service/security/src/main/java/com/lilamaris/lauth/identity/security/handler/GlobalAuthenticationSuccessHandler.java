package com.lilamaris.lauth.identity.security.handler;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.in.IssueTokenPairUseCase;
import com.lilamaris.lauth.kenel.web.response.ServletResponseWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@NullMarked
@Component
@RequiredArgsConstructor
public class GlobalAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final IssueTokenPairUseCase issueTokenPairUseCase;
    private final ServletResponseWriter responseWriter;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (!(authentication.getPrincipal() instanceof UserPrincipal principal)) return;

        try {
            var tokenPair = issueTokenPairUseCase.issue(principal.userId());
            responseWriter.write(response, HttpStatus.OK, tokenPair);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }
    }
}
