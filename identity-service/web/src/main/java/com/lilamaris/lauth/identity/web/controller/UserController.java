package com.lilamaris.lauth.identity.web.controller;

import com.lilamaris.lauth.identity.application.model.user.UserPrincipal;
import com.lilamaris.lauth.identity.application.port.in.UpdateDisplayNameUseCase;
import com.lilamaris.lauth.identity.application.port.in.UpdateHandleUseCase;
import com.lilamaris.lauth.identity.application.port.in.result.UpdateDisplayNameResult;
import com.lilamaris.lauth.identity.application.port.in.result.UpdateHandleResult;
import com.lilamaris.lauth.identity.web.controller.request.UpdateDisplayNameRequest;
import com.lilamaris.lauth.identity.web.controller.request.UpdateHandleRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UpdateHandleUseCase updateHandleUseCase;
    private final UpdateDisplayNameUseCase updateDisplayNameUseCase;

    @PreAuthorize("hasAuthority('SCOPE_user.write')")
    @PatchMapping("/display-name")
    public ResponseEntity<UpdateDisplayNameResult> updateDisplayName(
            @Valid @RequestBody UpdateDisplayNameRequest body,
            @AuthenticationPrincipal(expression = "user") UserPrincipal user
    ) {
        var userId = user.userId();
        var command = body.toCommand(userId);
        var result = updateDisplayNameUseCase.update(command);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAuthority('SCOPE_user.write')")
    @PatchMapping("/handle")
    public ResponseEntity<UpdateHandleResult> updateHandle(
            @Valid @RequestBody UpdateHandleRequest body,
            @AuthenticationPrincipal(expression = "user") UserPrincipal user
    ) {
        var command = body.toCommand(user.userId());
        var result = updateHandleUseCase.update(command);
        return ResponseEntity.ok(result);
    }
}
