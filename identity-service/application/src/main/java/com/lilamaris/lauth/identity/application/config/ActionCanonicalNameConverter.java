package com.lilamaris.lauth.identity.application.config;

import com.lilamaris.lauth.identity.domain.scope.Action;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class ActionCanonicalNameConverter implements Converter<String, Action> {
    @Override
    public Action convert(@NonNull String source) {
        if (source.isBlank()) throw new IllegalArgumentException("Source must not be blank.");
        return Action.from(source);
    }
}
