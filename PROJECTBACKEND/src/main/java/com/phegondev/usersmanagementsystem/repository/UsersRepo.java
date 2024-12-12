package com.phegondev.usersmanagementsystem.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.phegondev.usersmanagementsystem.Model.UsersAccounts;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<UsersAccounts, Integer> {

    Optional<UsersAccounts> findByEmail(String email);
    Optional<UsersAccounts> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
