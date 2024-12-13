package com.phegondev.usersmanagementsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.phegondev.usersmanagementsystem.Model.ForgotPassword;
import com.phegondev.usersmanagementsystem.Model.UsersAccounts;

public interface ForgotPasswordRepo extends JpaRepository<ForgotPassword, Integer> {
    
     @Query("SELECT fp FROM ForgotPassword fp WHERE fp.otp = :otp AND fp.usersAccounts = :usersAccounts")
    Optional<ForgotPassword> findByOtpAndUsersAccounts(
        @Param("otp") Integer otp, 
        @Param("usersAccounts") UsersAccounts usersAccounts
    );

}
