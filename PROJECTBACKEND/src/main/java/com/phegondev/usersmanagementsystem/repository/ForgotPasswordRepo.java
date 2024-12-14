package com.phegondev.usersmanagementsystem.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.phegondev.usersmanagementsystem.Model.ForgotPassword;

public interface ForgotPasswordRepo extends JpaRepository<ForgotPassword, Integer> {

    List<ForgotPassword> findByExpirationTimesBefore(LocalDateTime now);
    
   @Query("SELECT fp FROM ForgotPassword fp WHERE fp.otp = :otp")
    List<ForgotPassword> findByOtp(@Param("otp") Integer otp);

    @Query("SELECT fp FROM ForgotPassword fp WHERE fp.otp = :otp AND fp.usersAccounts.id = :userId")
    Optional<ForgotPassword> findByOtpAndUserId(
        @Param("otp") Integer otp, 
        @Param("userId") Integer userId
    );
}
