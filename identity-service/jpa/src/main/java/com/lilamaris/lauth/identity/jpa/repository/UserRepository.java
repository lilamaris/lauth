package com.lilamaris.lauth.identity.jpa.repository;

import com.lilamaris.lauth.identity.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
