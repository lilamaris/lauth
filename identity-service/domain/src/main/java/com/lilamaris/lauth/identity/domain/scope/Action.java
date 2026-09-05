package com.lilamaris.lauth.identity.domain.scope;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum Action {
    READ("read"),
    WRITE("write");

    private static final Map<String, Action> NAME_MAP = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(
                    Action::canonicalName,
                    Function.identity()
            ));
    private final String canonicalName;

    public static Action from(String canonicalName) {
        var action = NAME_MAP.get(canonicalName);
        if (action == null) throw new IllegalArgumentException("Unknown action canonical name. name=" + canonicalName);
        return action;
    }

    public String canonicalName() {
        return canonicalName;
    }
}
