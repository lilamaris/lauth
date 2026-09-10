package com.lilamaris.lauth.kenel.web.response.error;

import com.lilamaris.lauth.kernel.application.exception.ProgressType;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ErrorStatusRegistrar {
    private final List<ErrorStatusMapping> mappings = new ArrayList<>();

    public ErrorStatusMatcher type(ProgressType... types) {
        return new ErrorStatusMatcher(
                this,
                Arrays.stream(types).map(ProgressType::canonicalName).toList()
        );
    }

    public ErrorStatusMatcher type(String... types) {
        return new ErrorStatusMatcher(this, List.of(types));
    }

    List<ErrorStatusMapping> mappings() {
        return List.copyOf(mappings);
    }

    private void add(List<String> types, HttpStatus status) {
        for (var type : types) {
            mappings.add(new ErrorStatusMapping(type, status));
        }
    }

    public static class ErrorStatusMatcher {
        private final ErrorStatusRegistrar registrar;
        private final List<String> types;

        ErrorStatusMatcher(ErrorStatusRegistrar registrar, List<String> types) {
            this.registrar = registrar;
            this.types = types;
        }

        public ErrorStatusRegistrar mapsTo(HttpStatus status) {
            registrar.add(types, status);
            return registrar;
        }
    }
}
