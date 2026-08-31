package com.lilamaris.cozyr.kernel.core.condition;

import java.util.Objects;

public class ObjectPrecondition {
    public static <T> T requireNonNull(T value, String name) {
        Objects.requireNonNull(value, name + " must not be null.");
        return value;
    }
}
