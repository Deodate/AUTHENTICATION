package com.RESTAPISPRINGBOOTREACT.BACKEND.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.RESTAPISPRINGBOOTREACT.BACKEND.entity.Userstbl;

public interface UsersRepository extends JpaRepository<Userstbl, UUID> {

    Optional<Userstbl> findByEmail(String email);
}
