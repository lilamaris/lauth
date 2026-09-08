package com.lilamaris.lauth.identity.application.service;

import com.lilamaris.lauth.identity.application.exception.IdentityServiceProgressCode;
import com.lilamaris.lauth.identity.application.internal.jwt.TokenService;
import com.lilamaris.lauth.identity.application.model.jwt.TokenPair;
import com.lilamaris.lauth.identity.application.port.in.IssueTokenPairUseCase;
import com.lilamaris.lauth.identity.application.port.out.UserPrincipalReader;
import com.lilamaris.shrturl.kernel.application.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IssueTokenPairService implements IssueTokenPairUseCase {
    private final UserPrincipalReader userPrincipalReader;
    private final TokenService tokenService;

    @Override
    public TokenPair issue(UUID userId) {
        var principal = userPrincipalReader.findPrincipalById(userId)
                .orElseThrow(() -> new ApplicationException(IdentityServiceProgressCode.USER_NOT_FOUND));

        var accessToken = tokenService.createAccessToken(principal);
        var refreshToken = tokenService.createRefreshToken();

        return TokenPair.of(accessToken, refreshToken);
    }
}
