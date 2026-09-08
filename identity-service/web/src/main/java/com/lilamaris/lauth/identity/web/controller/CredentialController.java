package com.lilamaris.lauth.identity.web.controller;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.in.RegisterCredentialUseCase;
import com.lilamaris.lauth.identity.web.controller.request.RegisterCredentialRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class CredentialController {
    private final RegisterCredentialUseCase registerCredentialUseCase;

    @PostMapping("/sign-up")
    public ResponseEntity<UserPrincipal> signUp(
            @Valid @RequestBody RegisterCredentialRequest request
    ) {
        var command = request.toCommand();
        var result = registerCredentialUseCase.register(command);
        return ResponseEntity.ok(result);
    }
}
