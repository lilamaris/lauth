package com.lilamaris.cozyr.kernel.core.condition;

public class NumberPrecondition {
    public static Integer requireNegative(Integer value, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        if (value >= 0) throw new IllegalArgumentException(name + " must be negative.");
        return value;
    }

    public static Integer requireNonNegative(Integer value, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        if (value < 0) throw new IllegalArgumentException(name + " must be non-negative.");
        return value;
    }

    public static Integer requirePositive(Integer value, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        if (value <= 0) throw new IllegalArgumentException(name + " must be positive.");
        return value;
    }

    public static Integer requireNonPositive(Integer value, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        if (value > 0) throw new IllegalArgumentException(name + " must be non-positive.");
        return value;
    }

    public static Integer requireAtLeast(Integer value, Integer minimum, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        ObjectPrecondition.requireNonNull(minimum, "minimum");
        if (value < minimum) throw new IllegalArgumentException(name + " must be at least " + minimum + ".");
        return value;
    }

    public static Integer requireAtMost(Integer value, Integer maximum, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        ObjectPrecondition.requireNonNull(maximum, "maximum");
        if (value > maximum) throw new IllegalArgumentException(name + " must be at most " + maximum + ".");
        return value;
    }

    public static Integer requireBetween(Integer value, Integer minimum, Integer maximum, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        ObjectPrecondition.requireNonNull(minimum, "minimum");
        ObjectPrecondition.requireNonNull(maximum, "maximum");
        if (minimum > maximum) throw new IllegalArgumentException("minimum must be less than or equal to maximum.");
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(name + " must be between " + minimum + " and " + maximum + ".");
        }
        return value;
    }

    public static Long requireNegative(Long value, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        if (value >= 0) throw new IllegalArgumentException(name + " must be negative.");
        return value;
    }

    public static Long requireNonNegative(Long value, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        if (value < 0) throw new IllegalArgumentException(name + " must be non-negative.");
        return value;
    }

    public static Long requirePositive(Long value, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        if (value <= 0) throw new IllegalArgumentException(name + " must be positive.");
        return value;
    }

    public static Long requireNonPositive(Long value, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        if (value > 0) throw new IllegalArgumentException(name + " must be non-positive.");
        return value;
    }

    public static Long requireAtLeast(Long value, Long minimum, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        ObjectPrecondition.requireNonNull(minimum, "minimum");
        if (value < minimum) throw new IllegalArgumentException(name + " must be at least " + minimum + ".");
        return value;
    }

    public static Long requireAtMost(Long value, Long maximum, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        ObjectPrecondition.requireNonNull(maximum, "maximum");
        if (value > maximum) throw new IllegalArgumentException(name + " must be at most " + maximum + ".");
        return value;
    }

    public static Long requireBetween(Long value, Long minimum, Long maximum, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        ObjectPrecondition.requireNonNull(minimum, "minimum");
        ObjectPrecondition.requireNonNull(maximum, "maximum");
        if (minimum > maximum) throw new IllegalArgumentException("minimum must be less than or equal to maximum.");
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(name + " must be between " + minimum + " and " + maximum + ".");
        }
        return value;
    }
}
