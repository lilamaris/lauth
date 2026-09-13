package com.lilamaris.lauth.identity.web.controller;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.in.RegisterCredentialUseCase;
import com.lilamaris.lauth.identity.application.port.in.RequestPasswordResetUseCase;
import com.lilamaris.lauth.identity.application.port.in.ResetPasswordUseCase;
import com.lilamaris.lauth.identity.web.controller.request.RegisterCredentialRequest;
import com.lilamaris.lauth.identity.web.controller.request.RequestPasswordResetRequest;
import com.lilamaris.lauth.identity.web.controller.request.ResetPasswordRequest;
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
public class AuthController {
    private final RegisterCredentialUseCase registerCredentialUseCase;
    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;

    @PostMapping("/sign-up")
    public ResponseEntity<UserPrincipal> signUp(
            @Valid @RequestBody RegisterCredentialRequest request
    ) {
        var command = request.toCommand();
        var result = registerCredentialUseCase.register(command);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<Void> passwordReset(
            @Valid @RequestBody RequestPasswordResetRequest body
    ) {
        var command = body.toCommand();
        requestPasswordResetUseCase.request(command);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<Void> confirmPasswordReset(
            @Valid @RequestBody ResetPasswordRequest body
    ) {
        var command = body.toCommand();
        resetPasswordUseCase.reset(command);
        return ResponseEntity.noContent().build();
    }
}
