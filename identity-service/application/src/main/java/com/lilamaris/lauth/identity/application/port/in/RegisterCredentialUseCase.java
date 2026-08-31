package com.lilamaris.lauth.identity.application.port.in;

import com.lilamaris.lauth.identity.application.model.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.in.command.RegisterCredentialCommand;

public interface RegisterCredentialUseCase {
    UserPrincipal register(RegisterCredentialCommand command);
}
