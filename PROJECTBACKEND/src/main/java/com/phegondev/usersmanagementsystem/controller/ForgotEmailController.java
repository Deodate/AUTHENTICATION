package com.phegondev.usersmanagementsystem.Controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.phegondev.usersmanagementsystem.DTO.MailBody;
import com.phegondev.usersmanagementsystem.Model.ForgotPassword;
import com.phegondev.usersmanagementsystem.Model.UsersAccounts;
import com.phegondev.usersmanagementsystem.repository.ForgotPasswordRepo;
import com.phegondev.usersmanagementsystem.repository.UsersRepo;
import com.phegondev.usersmanagementsystem.service.EmailService;
import com.phegondev.usersmanagementsystem.utils.ChangePassword;

@RestController
@RequestMapping("/forgotPassword")
public class ForgotEmailController {

    private final UsersRepo usersRepo;
    private final EmailService emailService;
    private final ForgotPasswordRepo forgotPasswordRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ForgotEmailController(
            UsersRepo usersRepo,
            EmailService emailService,
            ForgotPasswordRepo forgotPasswordRepo,
            PasswordEncoder passwordEncoder) {
        this.usersRepo = usersRepo;
        this.emailService = emailService;
        this.forgotPasswordRepo = forgotPasswordRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email) {
        UsersAccounts usersAccounts = usersRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email!"));

        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("Lycee Project: This is the OTP : " + otp)
                .subject("Lycee Saint Alexandre Project / Forgot Password request")
                .build();

        LocalDateTime expirationTime = Instant.ofEpochMilli(System.currentTimeMillis() + 2 * 60 * 1000)
                .atOffset(ZoneOffset.UTC)
                .toLocalDateTime();

        // No need to set createdAt manually
        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTimes(expirationTime)
                .usersAccounts(usersAccounts)
                .build();

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepo.save(fp);
        return ResponseEntity.ok("Email sent for verification!");
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<Map<String, String>> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
        try {
            // Find the user first
            UsersAccounts usersAccounts = usersRepo.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email!"));

            // Find ForgotPassword entries by OTP
            List<ForgotPassword> forgotPasswordEntries = forgotPasswordRepo.findByOtp(otp);

            // Filter entries for the specific user
            forgotPasswordEntries = forgotPasswordEntries.stream()
                    .filter(fp -> fp.getUsersAccounts() != null &&
                            fp.getUsersAccounts().getId().equals(usersAccounts.getId()))
                    .collect(Collectors.toList());

            // Check if any entries exist
            if (forgotPasswordEntries.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Invalid OTP for this email");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Take the most recent entry
            ForgotPassword fp = forgotPasswordEntries.get(0);

            // Compare expirationTimes with the current time using isBefore
            if (fp.getExpirationTimes().isBefore(LocalDateTime.now())) {
                 fp.setExpirationTimes(LocalDateTime.now().plusMinutes(2));
                forgotPasswordRepo.deleteById(fp.getFpid());

                Map<String, String> response = new HashMap<>();
                response.put("message", "OTP has expired!");
                return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "OTP verified!");
            return ResponseEntity.ok(response);

        } catch (UsernameNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "An unexpected error occurred: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword,
            @PathVariable String email) {
        if (!Objects.equals(changePassword.password(), changePassword.repeatPassword())) {
            return new ResponseEntity<>("Passwords do not match!", HttpStatus.EXPECTATION_FAILED);
        }

        UsersAccounts usersAccounts = usersRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        String encodedPassword = passwordEncoder.encode(changePassword.password());
        usersAccounts.setPassword(encodedPassword);

        return ResponseEntity.ok("Password changed successfully!");
    }

    private Integer otpGenerator() {
        Random random = new Random();
        return random.nextInt(100_00, 999_999);
    }
}
