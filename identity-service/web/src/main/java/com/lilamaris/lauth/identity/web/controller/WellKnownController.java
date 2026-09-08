package com.lilamaris.lauth.identity.web.controller;

import com.lilamaris.lauth.identity.application.port.in.ListVerifiableJWKSUseCase;
import com.nimbusds.jose.jwk.JWK;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/.well-known")
@RequiredArgsConstructor
public class WellKnownController {
    private final ListVerifiableJWKSUseCase listVerifiableJWKSUseCase;

    @GetMapping("/jwks.json")
    public ResponseEntity<Map<String, Object>> listJWKS() {
        var keys = listVerifiableJWKSUseCase.list().stream()
                .map(JWK::toJSONObject)
                .toList();

        return ResponseEntity.ok(Map.of("keys", keys));
    }
}
