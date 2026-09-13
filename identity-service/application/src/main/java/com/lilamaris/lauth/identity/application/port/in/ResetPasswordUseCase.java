package com.lilamaris.lauth.identity.application.port.in;

import com.lilamaris.lauth.identity.application.port.in.command.ResetPasswordCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface ResetPasswordUseCase {
    void reset(@NotNull @Valid ResetPasswordCommand command);
}
