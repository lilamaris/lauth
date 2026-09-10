package com.lilamaris.lauth.identity.application.port.out;

import com.lilamaris.lauth.identity.domain.scope.Scope;

import java.util.Set;

public interface ScopeReader {
    Set<Scope> findAll();


}
