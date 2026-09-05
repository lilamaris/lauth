package com.lilamaris.lauth.identity.jpa.repository;

import com.lilamaris.lauth.identity.domain.scope.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScopeRepository extends JpaRepository<Scope, UUID> {
}
