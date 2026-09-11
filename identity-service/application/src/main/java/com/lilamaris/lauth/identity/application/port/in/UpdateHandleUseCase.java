package com.lilamaris.lauth.identity.application.port.in;

import com.lilamaris.lauth.identity.application.port.in.command.UpdateHandleCommand;
import com.lilamaris.lauth.identity.application.port.in.result.UpdateHandleResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface UpdateHandleUseCase {
    UpdateHandleResult update(@NotNull @Valid UpdateHandleCommand command);
}
