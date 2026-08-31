package com.lilamaris.cozyr.kernel.core.condition;

import java.util.Collection;
import java.util.Objects;

public class CollectionPrecondition {
    public static <E, C extends Collection<E>> C requireNonNullElements(C value, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        if (value.stream().anyMatch(Objects::isNull)) throw new IllegalArgumentException(name + " must not contain null elements.");
        return value;
    }
}
