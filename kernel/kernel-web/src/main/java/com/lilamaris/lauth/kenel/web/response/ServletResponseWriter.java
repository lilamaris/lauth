package com.lilamaris.lauth.kenel.web.response;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@RequiredArgsConstructor
public class ServletResponseWriter {
    private final ObjectMapper objectMapper;

    public void write(HttpServletResponse response, ProblemDetail problemDetail) throws IOException {
        write(response, HttpStatus.valueOf(problemDetail.getStatus()), problemDetail);
    }

    public void write(HttpServletResponse response, HttpStatus status, Object body) throws IOException {
        if (response == null) return;

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
