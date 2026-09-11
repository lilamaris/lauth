package com.lilamaris.lauth.identity.application.port.in;

import com.lilamaris.lauth.identity.application.port.in.command.RequestPasswordResetCommand;

public interface RequestPasswordResetUseCase {
    void request(RequestPasswordResetCommand command);
}
