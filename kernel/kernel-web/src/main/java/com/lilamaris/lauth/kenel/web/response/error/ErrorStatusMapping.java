package com.lilamaris.lauth.kenel.web.response.error;

import org.springframework.http.HttpStatus;

public record ErrorStatusMapping(
        String type,
        HttpStatus status
) {
}
