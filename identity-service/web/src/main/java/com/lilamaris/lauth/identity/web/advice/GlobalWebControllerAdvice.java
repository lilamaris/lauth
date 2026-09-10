package com.lilamaris.lauth.identity.web.advice;

import com.lilamaris.lauth.kenel.web.response.error.ProblemDetailFactory;
import com.lilamaris.lauth.kenel.web.response.error.StandardErrorDescriptor;
import com.lilamaris.lauth.kernel.application.exception.ApplicationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalWebControllerAdvice {
    private final ProblemDetailFactory problemDetailFactory;

    @ExceptionHandler(ApplicationException.class)
    public ProblemDetail handleApplicationException(ApplicationException exception, HttpServletRequest request) {
        var applicationCode = exception.getApplicationCode();
        log.warn("Handle ApplicationException. type={}, path={}, message={}", applicationCode.type(), request.getRequestURI(), applicationCode.message());
        return problemDetailFactory.from(applicationCode);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            BindException.class,
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class
    })
    public ProblemDetail handleBadRequest(Exception exception, HttpServletRequest request) {
        log.warn("Handle BadRequest. type={}, path={}, message={}", exception.getClass().getSimpleName(), request.getRequestURI(), exception.getMessage());
        return problemDetailFactory.from(StandardErrorDescriptor.BAD_REQUEST);
    }

    @ExceptionHandler({IllegalStateException.class})
    public ProblemDetail handleIllegalState(IllegalStateException exception, HttpServletRequest request) {
        log.warn("Handle IllegalState. type={}, path={}, message={}", exception.getClass().getSimpleName(), request.getRequestURI(), exception.getMessage());
        return problemDetailFactory.from(StandardErrorDescriptor.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({NoResourceFoundException.class})
    public ProblemDetail handleNotFound(NoResourceFoundException exception, HttpServletRequest request) {
        log.warn("Handle NotFound. type={}, path={}, message={}", exception.getClass().getSimpleName(), request.getRequestURI(), exception.getMessage());
        return problemDetailFactory.from(StandardErrorDescriptor.NOT_FOUND);
    }

    @ExceptionHandler({AuthorizationDeniedException.class})
    public ProblemDetail handleAccessDenied(Exception exception, HttpServletRequest request) {
        log.warn("Handle AccessDenied. type={}, path={}, message={}", exception.getClass().getSimpleName(), request.getRequestURI(), exception.getMessage());
        return problemDetailFactory.from(StandardErrorDescriptor.ACCESS_DENIED);
    }

    @ExceptionHandler({Exception.class})
    public ProblemDetail handleUnexpected(Exception exception, HttpServletRequest request) {
        log.warn("Handle Exception. type={}, path={}, message={}", exception.getClass().getSimpleName(), request.getRequestURI(), exception.getMessage());
        return problemDetailFactory.from(StandardErrorDescriptor.INTERNAL_SERVER_ERROR);
    }
}
