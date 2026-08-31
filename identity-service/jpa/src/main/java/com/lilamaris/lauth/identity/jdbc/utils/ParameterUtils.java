package com.lilamaris.lauth.identity.jdbc.utils;

public class ParameterUtils {
    public static String escape(String v) {
        return v
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
