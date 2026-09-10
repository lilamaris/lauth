package com.lilamaris.lauth.identity.application.port.out;

import com.lilamaris.lauth.identity.application.model.scope.ResourceScope;

import java.util.Set;
import java.util.UUID;

public interface UserGrantReader {
    Set<ResourceScope> findByUserId(UUID userId);
}
