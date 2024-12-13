package com.phegondev.usersmanagementsystem.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.phegondev.usersmanagementsystem.Model.UsersAccounts;

import jakarta.persistence.ManyToOne;
import jakarta.transaction.Transactional;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<UsersAccounts, Integer> {

    Optional<UsersAccounts> findByEmail(String email);
    Optional<UsersAccounts> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    @Transactional
    @ManyToOne
     @Query("UPDATE UsersAccounts u SET u.password = ?2  WHERE u.email = ?1")
    void updatePassword(String email, String password);

}
