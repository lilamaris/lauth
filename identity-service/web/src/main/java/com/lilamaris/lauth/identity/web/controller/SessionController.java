package com.lilamaris.lauth.identity.web.controller;

import com.lilamaris.lauth.identity.application.model.jwt.TokenPair;
import com.lilamaris.lauth.identity.application.port.in.RefreshSessionUseCase;
import com.lilamaris.lauth.identity.web.controller.request.RefreshSessionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/session")
@RequiredArgsConstructor
public class SessionController {
    private final RefreshSessionUseCase refreshSessionUseCase;

    @PostMapping("/refresh")
    public ResponseEntity<TokenPair> refresh(
            @Valid @RequestBody RefreshSessionRequest body
    ) {
        var command = body.toCommand();
        var result = refreshSessionUseCase.refresh(command);
        return ResponseEntity.ok(result);
    }
}
