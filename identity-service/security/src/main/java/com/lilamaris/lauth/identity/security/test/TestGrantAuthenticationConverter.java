package com.lilamaris.lauth.identity.security.test;

import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.UUID;

@NullMarked
public class TestGrantAuthenticationConverter implements AuthenticationConverter {
    @Override
    public @Nullable Authentication convert(HttpServletRequest request) {
        var grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);

        if (!TestGrantType.TEST_GRANT_TYPE.getValue().equals(grantType)) {
            return null;
        }

        var values = request.getParameterValues("user_id");
        if (values == null || values.length != 1 || !StringUtils.hasText(values[0])) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
        }

        UUID parsedId;
        try {
            parsedId = UUID.fromString(values[0]);
        } catch (IllegalArgumentException exception) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
        }
        var clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        return new TestGrantAuthenticationToken(parsedId, clientPrincipal);
    }
}
