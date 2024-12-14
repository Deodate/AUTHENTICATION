package com.phegondev.usersmanagementsystem.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_otp")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer lotpid;  // Use the existing lotpid as primary key

    @Column(name = "otp", nullable = false)
    private Integer otp;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UsersAccounts usersAccounts;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expiration_times", nullable = false)
    private LocalDateTime expirationTimes;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;
}