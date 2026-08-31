package com.lilamaris.shrturl.kernel.application.model.cursor;

import com.lilamaris.cozyr.kernel.core.condition.NumberPrecondition;
import org.jspecify.annotations.Nullable;


public record CursorRequest<C>(
        @Nullable C cursor,
        int size
) {
    public CursorRequest {
        NumberPrecondition.requirePositive(size, "size");
    }

    public static <C> CursorRequest<C> of(C cursor, int size) {
        return new CursorRequest<>(cursor, size);
    }

    public boolean isFirstPage() {
        return cursor == null;
    }
}
