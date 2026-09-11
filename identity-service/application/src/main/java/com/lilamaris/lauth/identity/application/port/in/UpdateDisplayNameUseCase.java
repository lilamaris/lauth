package com.lilamaris.lauth.identity.application.port.in;

import com.lilamaris.lauth.identity.application.port.in.command.UpdateDisplayNameCommand;
import com.lilamaris.lauth.identity.application.port.in.result.UpdateDisplayNameResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface UpdateDisplayNameUseCase {
    UpdateDisplayNameResult update(@NotNull @Valid UpdateDisplayNameCommand command);
}
