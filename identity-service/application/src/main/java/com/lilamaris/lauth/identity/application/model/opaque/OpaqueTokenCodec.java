package com.lilamaris.lauth.identity.application.model.opaque;

import com.lilamaris.cozyr.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.cozyr.kernel.core.condition.StringPrecondition;

import java.util.regex.Pattern;

public class OpaqueTokenCodec {
    private final static String SEPARATOR = ".";

    public static OpaqueToken decode(String raw) {
        StringPrecondition.requireNonBlank(raw, "raw");
        var parts = raw.split(Pattern.quote(SEPARATOR), -1);
        if (parts.length != 2) throw new IllegalArgumentException("Not valid opaque token format.");
        var selector = parts[0].trim();
        var token = parts[1].trim();

        if (selector.isEmpty() || token.isEmpty())
            throw new IllegalArgumentException("Selector or token part must not be empty.");

        return OpaqueToken.of(selector, token);
    }

    public static String encode(OpaqueToken token) {
        ObjectPrecondition.requireNonNull(token, "token");
        return token.selector() + SEPARATOR + token.value();
    }

    public static String encode(Object selector, String value) {
        ObjectPrecondition.requireNonNull(selector, "selector");
        StringPrecondition.requireNonBlank(value, "value");
        return selector.toString() + SEPARATOR + value;
    }
}
