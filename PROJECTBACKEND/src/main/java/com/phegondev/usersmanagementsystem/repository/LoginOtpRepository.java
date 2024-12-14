package com.phegondev.usersmanagementsystem.repository;

import com.phegondev.usersmanagementsystem.Model.LoginOtp;
import com.phegondev.usersmanagementsystem.Model.UsersAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LoginOtpRepository extends JpaRepository<LoginOtp, Integer> {
    @Query("SELECT lo FROM LoginOtp lo WHERE lo.otp = :otp AND lo.usersAccounts.email = :email AND lo.expirationTimes > :currentTime")
    Optional<LoginOtp> findValidOtpByEmailAndOtp(
            @Param("otp") Long otp,
            @Param("email") String email,
            @Param("currentTime") LocalDateTime currentTime);

    void deleteByUsersAccounts(UsersAccounts usersAccounts);
}