package com.lilamaris.shrturl.kernel.application.exception;

public record StandardProgressType(String canonicalName) implements ProgressType {
    public static final StandardProgressType BAD_REQUEST = new StandardProgressType("bad-request");

    public static final StandardProgressType NOT_FOUND = new StandardProgressType("not-found");

    public static final StandardProgressType DUPLICATED = new StandardProgressType("duplicated");

    public static final StandardProgressType ACCESS_DENIED = new StandardProgressType("access-denied");

    public static final StandardProgressType UNAUTHORIZED = new StandardProgressType("unauthorized");

    public static final StandardProgressType FAILED = new StandardProgressType("failed");
}
