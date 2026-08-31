package com.lilamaris.cozyr.kernel.core.condition;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public class TimePrecondition {
    public static Instant requireBefore(Instant subject, Instant upperBound, String subjectName, String upperBoundName) {
        ObjectPrecondition.requireNonNull(subject, subjectName);
        ObjectPrecondition.requireNonNull(upperBound, upperBoundName);
        if (!subject.isBefore(upperBound)) {
            throw new IllegalArgumentException(subjectName + " must be before " + upperBoundName);
        }
        return subject;
    }

    public static Instant requireBeforeOrEqual(Instant subject, Instant upperBound, String subjectName, String upperBoundName) {
        ObjectPrecondition.requireNonNull(subject, subjectName);
        ObjectPrecondition.requireNonNull(upperBound, upperBoundName);
        if (subject.isAfter(upperBound)) {
            throw new IllegalArgumentException(subjectName + " must be before or equal " + upperBoundName);
        }
        return subject;
    }

    public static Instant requireAfter(Instant subject, Instant lowerBound, String subjectName, String lowerBoundName) {
        ObjectPrecondition.requireNonNull(subject, subjectName);
        ObjectPrecondition.requireNonNull(lowerBound, lowerBoundName);
        if (!subject.isAfter(lowerBound)) {
            throw new IllegalArgumentException(subjectName + " must be after " + lowerBoundName);
        }
        return subject;
    }

    public static Instant requireAfterOrEqual(Instant subject, Instant lowerBound, String subjectName, String lowerBoundName) {
        ObjectPrecondition.requireNonNull(subject, subjectName);
        ObjectPrecondition.requireNonNull(lowerBound, lowerBoundName);
        if (subject.isBefore(lowerBound)) {
            throw new IllegalArgumentException(subjectName + " must be before or equal " + lowerBoundName);
        }
        return subject;
    }

    public static LocalDate requireBefore(LocalDate subject, LocalDate upperBound, String subjectName, String upperBoundName) {
        ObjectPrecondition.requireNonNull(subject, subjectName);
        ObjectPrecondition.requireNonNull(upperBound, upperBoundName);
        if (!subject.isBefore(upperBound)) {
            throw new IllegalArgumentException(subjectName + " must be before " + upperBoundName);
        }
        return subject;
    }

    public static LocalDate requireBeforeOrEqual(LocalDate subject, LocalDate upperBound, String subjectName, String upperBoundName) {
        ObjectPrecondition.requireNonNull(subject, subjectName);
        ObjectPrecondition.requireNonNull(upperBound, upperBoundName);
        if (subject.isAfter(upperBound)) {
            throw new IllegalArgumentException(subjectName + " must be before or equal " + upperBoundName);
        }
        return subject;
    }

    public static LocalDate requireAfter(LocalDate subject, LocalDate lowerBound, String subjectName, String lowerBoundName) {
        ObjectPrecondition.requireNonNull(subject, subjectName);
        ObjectPrecondition.requireNonNull(lowerBound, lowerBoundName);
        if (!subject.isAfter(lowerBound)) {
            throw new IllegalArgumentException(subjectName + " must be after " + lowerBoundName);
        }
        return subject;
    }

    public static LocalDate requireAfterOrEqual(LocalDate subject, LocalDate lowerBound, String subjectName, String lowerBoundName) {
        ObjectPrecondition.requireNonNull(subject, subjectName);
        ObjectPrecondition.requireNonNull(lowerBound, lowerBoundName);
        if (subject.isBefore(lowerBound)) {
            throw new IllegalArgumentException(subjectName + " must be after or equal " + lowerBoundName);
        }
        return subject;
    }

    public static LocalTime requireBefore(LocalTime subject, LocalTime upperBound, String subjectName, String upperBoundName) {
        ObjectPrecondition.requireNonNull(subject, subjectName);
        ObjectPrecondition.requireNonNull(upperBound, upperBoundName);
        if (!subject.isBefore(upperBound)) {
            throw new IllegalArgumentException(subjectName + " must be before " + upperBoundName);
        }
        return subject;
    }

    public static LocalTime requireBeforeOrEqual(LocalTime subject, LocalTime upperBound, String subjectName, String upperBoundName) {
        ObjectPrecondition.requireNonNull(subject, subjectName);
        ObjectPrecondition.requireNonNull(upperBound, upperBoundName);
        if (subject.isAfter(upperBound)) {
            throw new IllegalArgumentException(subjectName + " must be before or equal " + upperBoundName);
        }
        return subject;
    }

    public static LocalTime requireAfter(LocalTime subject, LocalTime lowerBound, String subjectName, String lowerBoundName) {
        ObjectPrecondition.requireNonNull(subject, subjectName);
        ObjectPrecondition.requireNonNull(lowerBound, lowerBoundName);
        if (!subject.isAfter(lowerBound)) {
            throw new IllegalArgumentException(subjectName + " must be after " + lowerBoundName);
        }
        return subject;
    }

    public static LocalTime requireAfterOrEqual(LocalTime subject, LocalTime lowerBound, String subjectName, String lowerBoundName) {
        ObjectPrecondition.requireNonNull(subject, subjectName);
        ObjectPrecondition.requireNonNull(lowerBound, lowerBoundName);
        if (subject.isBefore(lowerBound)) {
            throw new IllegalArgumentException(subjectName + " must be after or equal " + lowerBoundName);
        }
        return subject;
    }

    public static Duration requireNegative(Duration value, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        if (!value.isNegative()) throw new IllegalArgumentException(name + " must be negative.");
        return value;
    }

    public static Duration requireNonNegative(Duration value, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        if (value.isNegative()) throw new IllegalArgumentException(name + " must be non-negative.");
        return value;
    }

    public static Duration requirePositive(Duration value, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        if (!value.isPositive()) throw new IllegalArgumentException(name + " must be positive.");
        return value;
    }

    public static Duration requireNonPositive(Duration value, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        if (value.isPositive()) throw new IllegalArgumentException(name + " must be non-positive.");
        return value;
    }

    public static Duration requireAtLeast(Duration value, Duration minimum, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        ObjectPrecondition.requireNonNull(minimum, "minimum");
        if (value.compareTo(minimum) < 0)
            throw new IllegalArgumentException(name + " must be at least " + minimum + ".");
        return value;
    }

    public static Duration requireAtMost(Duration value, Duration maximum, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        ObjectPrecondition.requireNonNull(maximum, "maximum");
        if (value.compareTo(maximum) > 0)
            throw new IllegalArgumentException(name + " must be at most " + maximum + ".");
        return value;
    }

    public static Duration requireBetween(Duration value, Duration minimum, Duration maximum, String name) {
        ObjectPrecondition.requireNonNull(value, name);
        ObjectPrecondition.requireNonNull(minimum, "minimum");
        ObjectPrecondition.requireNonNull(maximum, "maximum");
        if (minimum.compareTo(maximum) > 0) {
            throw new IllegalArgumentException("minimum must be less than or equal to maximum.");
        }
        if (value.compareTo(minimum) < 0 || value.compareTo(maximum) > 0) {
            throw new IllegalArgumentException(name + " must be between " + minimum + " and " + maximum + ".");
        }
        return value;
    }
}
