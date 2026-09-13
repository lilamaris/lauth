package com.lilamaris.lauth.identity.application.port.in;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.in.command.ResolveFederatedAccountCommand;

public interface ResolveFederatedAccountUseCase {
    UserPrincipal resolve(ResolveFederatedAccountCommand command);
}
