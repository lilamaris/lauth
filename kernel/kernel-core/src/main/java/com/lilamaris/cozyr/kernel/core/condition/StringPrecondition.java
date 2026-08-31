package com.lilamaris.cozyr.kernel.core.condition;

public class StringPrecondition {
    public static String requireNonBlank(String value, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        if (value.isBlank()) throw new IllegalArgumentException(name + " must not be blank.");
        return value;
    }

    public static String requireNotContain(String value, String s, String name) {
        requireNonBlank(value, name);
        requireNonBlank(s, "s");
        if (value.contains(s)) throw new IllegalArgumentException(name + " must not contain '" + s + "'.");
        return value;
    }
}
