package com.lilamaris.lauth.identity.application.model.execute;

import com.lilamaris.cozyr.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.shrturl.kernel.application.exception.ApplicationCode;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public record ExecuteOutcome<OUT>(
        boolean success,
        @Nullable OUT rawData,
        @Nullable ApplicationCode code
) {
    public ExecuteOutcome {
        if (success == (code != null))
            throw new IllegalArgumentException("code must be present only when execution failed.");
        if (success == (rawData == null))
            throw new IllegalArgumentException("data must be present only when execution successes.");
    }

    public static <OUT> ExecuteOutcome<OUT> success(OUT data) {
        ObjectPrecondition.requireNonNull(data, "data");
        return new ExecuteOutcome<>(true, data, null);
    }

    public static <OUT> ExecuteOutcome<OUT> failure(ApplicationCode code) {
        return new ExecuteOutcome<>(false, null, code);
    }

    public Optional<OUT> data() {
        return Optional.ofNullable(rawData);
    }
}
