package com.lilamaris.lauth.identity.application.port.in;

import com.lilamaris.lauth.identity.application.model.jwt.TokenPair;
import com.lilamaris.lauth.identity.application.port.in.command.RefreshSessionCommand;

public interface RefreshSessionUseCase {
    TokenPair refresh(RefreshSessionCommand command);
}
