package com.lilamaris.lauth.identity.application.port.in;

import com.lilamaris.lauth.identity.application.model.jwt.TokenPair;
import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;

public interface LoginSessionUseCase {
    TokenPair login(UserPrincipal principal);
}
