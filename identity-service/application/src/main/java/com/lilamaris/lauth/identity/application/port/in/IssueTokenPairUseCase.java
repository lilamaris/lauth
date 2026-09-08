package com.lilamaris.lauth.identity.application.port.in;

import com.lilamaris.lauth.identity.application.model.jwt.TokenPair;

import java.util.UUID;

public interface IssueTokenPairUseCase {
    TokenPair issue(UUID userId);
}
