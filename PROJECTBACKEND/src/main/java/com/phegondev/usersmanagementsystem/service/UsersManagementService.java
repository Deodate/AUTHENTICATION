package com.phegondev.usersmanagementsystem.service;

import com.phegondev.usersmanagementsystem.DTO.MailBody;
import com.phegondev.usersmanagementsystem.DTO.ReqRes;
import com.phegondev.usersmanagementsystem.Model.LoginOtp;
import com.phegondev.usersmanagementsystem.Model.UsersAccounts;
import com.phegondev.usersmanagementsystem.repository.LoginOtpRepository;
import com.phegondev.usersmanagementsystem.repository.UsersRepo;

import jakarta.persistence.Column;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UsersManagementService {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private LoginOtpRepository loginOtpRepository;

    // Register Method
    public ReqRes register(ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();

        try {
            // Check if email already exists
            if (usersRepo.existsByEmail(registrationRequest.getEmail())) {
                resp.setStatusCode(400);
                resp.setMessage("Email already exists");
                resp.setError("Duplicate Email");
                return resp;
            }

            // Check if phone already exists
            if (usersRepo.existsByPhone(registrationRequest.getPhone())) {
                resp.setStatusCode(400);
                resp.setMessage("Phone number already exists");
                resp.setError("Duplicate Phone");
                return resp;
            }

            // Create new user if email and phone are unique
            UsersAccounts ourUser = new UsersAccounts();
            ourUser.setEmail(registrationRequest.getEmail());
            ourUser.setFullname(registrationRequest.getFullname());
            ourUser.setPhone(registrationRequest.getPhone());
            ourUser.setNationality(registrationRequest.getNationality());
            ourUser.setPosition(registrationRequest.getPosition());
            ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

            UsersAccounts savedUser = usersRepo.save(ourUser);

            resp.setUsersAccounts(savedUser);
            resp.setMessage("User Saved Successfully");
            resp.setStatusCode(200);

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
            resp.setMessage("Registration failed");
        }

        return resp;
    }

    // Login Method
    public ReqRes login(ReqRes loginRequest) {
        ReqRes response = new ReqRes();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            var user = usersRepo.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

            response.setStatusCode(200);
            response.setToken(jwt);
            response.setPosition(user.getPosition()); // Changed from setRole()
            response.setFullname(user.getFullname()); // Added fullName
            response.setPhone(user.getPhone()); // Added phone
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }

        return response;
    }

    public ReqRes updateUser(Integer userId, UsersAccounts updatedUser) {
        ReqRes reqRes = new ReqRes();

        try {
            Optional<UsersAccounts> userOptional = usersRepo.findById(userId);

            if (userOptional.isPresent()) {
                UsersAccounts existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setFullname(updatedUser.getFullname()); // Changed from setName()
                existingUser.setPhone(updatedUser.getPhone());
                existingUser.setNationality(updatedUser.getNationality()); // Changed from setCity()
                existingUser.setPosition(updatedUser.getPosition()); // Changed from setRole()

                 // Update password if present
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                UsersAccounts savedUser = usersRepo.save(existingUser);
                reqRes.setUsersAccounts(savedUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User updated successfull!");

            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }

        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating user: " + e.getMessage());
        }

        return reqRes;
    }

    // Placeholder methods (unchanged)
    public ReqRes getMyInfo(String email) {
        ReqRes resp = new ReqRes();

        try{
           Optional<UsersAccounts> user = usersRepo.findByEmail(email);

            if (user.isPresent()) {
            resp.setUsersAccounts(user.get());
            resp.setStatusCode(200);
            resp.setMessage("User found Successful");
            
        } else {
            resp.setMessage("User not found");
            resp.setStatusCode(404);
            resp.setError("No such user exists");
        }

        } catch(Exception e){
            resp.setStatusCode(500);
            resp.setMessage("Error Occured while getting user info: " + e.getMessage());
        }
      
        return resp;
    }

    // UsersManagementService.java

    public ReqRes getAllUsers() {
        ReqRes resp = new ReqRes();

        try {
            // Fetch all users from the repository
            List<UsersAccounts> allUsers = usersRepo.findAll();

            if (!allUsers.isEmpty()) {
                resp.setUsersAccountsList(allUsers);
                resp.setStatusCode(200);
                resp.setMessage("Successful!");
            } else {
                resp.setStatusCode(404);
                resp.setMessage("No users found.");
            }
            return resp;

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setMessage("Error retrieving users: " + e.getMessage());
            return resp;
        }

    }

    public ReqRes deleteUser(Integer userId, String position) {
        ReqRes resp = new ReqRes();

        try {

            if(!"ADMIN".equals(position)){
                resp.setStatusCode(4053);
                 resp.setMessage("Access Denied. Admin privileges required.");
                return resp;
            }

            // Check if the user exists
            Optional<UsersAccounts> userOptional = usersRepo.findById(userId);

            if (userOptional.isPresent()) {
                // Delete the user
                usersRepo.deleteById(userId);
                resp.setStatusCode(200);
                resp.setMessage("User Deleted Successfully!");
                // usersRepo.delete(userOptional.get());
                // Set success response
                // resp.setStatusCode(200);
                // resp.setMessage("User deleted successfully");
            } else {
                // User not found
                resp.setStatusCode(404);
                resp.setMessage("User not found for deletion");
            }

        } catch (Exception e) {
            // Handle exceptions
            resp.setStatusCode(500);
            resp.setMessage("Error occurred while deleting user: " + e.getMessage());
        }

        return resp;
    }

    

    // Login 2FA

    // LoginOtp entity
    @Column(nullable = false)
    private Integer otp;

    // Corrected initiateLogin method
    public ReqRes initiateLogin(ReqRes req) {
        ReqRes response = new ReqRes();
        try {
            // Find the user using the correct repository method
            Optional<UsersAccounts> userOptional = usersRepo.findByEmail(req.getEmail());

            // Check if user exists
            if (userOptional.isEmpty()) {
                response.setStatusCode(404);
                response.setMessage("User not found");
                return response;
            }

            UsersAccounts user = userOptional.get();

            // Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.getEmail(),
                            req.getPassword()));

            // Generate OTP
            int otp = generateOtp();

            // Remove any existing OTPs for this user
            loginOtpRepository.deleteByUsersAccounts(user);

            // Create new LoginOtp with explicit field setting
            LoginOtp loginOtp = new LoginOtp();
            loginOtp.setUsersAccounts(user);
            loginOtp.setOtp(otp);
            loginOtp.setCreatedAt(LocalDateTime.now());
            loginOtp.setExpirationTimes(LocalDateTime.now().plusMinutes(10));
            loginOtp.setIsVerified(false);

            // Save the LoginOtp
            LoginOtp savedLoginOtp = loginOtpRepository.save(loginOtp);

            // Send OTP via email
            MailBody mailBody = MailBody.builder()
                    .to(user.getEmail())
                    .subject("Login OTP for Lycee Project")
                    .text("Your login OTP is: " + otp + ". This OTP will expire in 10 minutes.")
                    .build();
            emailService.sendSimpleMessage(mailBody);

            // Prepare response
            response.setStatusCode(200);
            response.setMessage("OTP sent to email. Please verify.");
            response.setEmail(user.getEmail());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Login initiation failed: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    // Helper method to generate OTP
    private Integer generateOtp() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }

    // public ReqRes verifyLoginOtp(ReqRes otpVerificationRequest) {
    // ReqRes response = new ReqRes();

    // try {
    // // Find valid OTP
    // Optional<LoginOtp> loginOtpOptional =
    // loginOtpRepository.findValidOtpByEmailAndOtp(
    // otpVerificationRequest.getOtp(),
    // otpVerificationRequest.getEmail(),
    // LocalDateTime.now());

    // if (loginOtpOptional.isPresent()) {
    // LoginOtp loginOtp = loginOtpOptional.get();
    // var user = loginOtp.getUsersAccounts();

    // // Mark OTP as verified
    // loginOtp.setIsVerified(true);
    // loginOtpRepository.save(loginOtp);

    // // Generate JWT tokens
    // var jwt = jwtUtils.generateToken(user);
    // var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

    // // Prepare response
    // response.setStatusCode(200);
    // response.setToken(jwt);
    // response.setRefreshToken(refreshToken);
    // response.setPosition(user.getPosition());
    // response.setFullname(user.getFullname());
    // response.setEmail(user.getEmail());
    // response.setMessage("Login Successful");
    // response.setExpirationTime("24Hrs");

    // } else {
    // response.setStatusCode(400);
    // response.setMessage("Invalid or expired OTP");
    // }

    // } catch (Exception e) {
    // response.setStatusCode(500);
    // response.setMessage("OTP verification failed: " + e.getMessage());
    // }

    // return response;
    // }

    public Object getUsersById(Integer userId) {
        throw new UnsupportedOperationException("Unimplemented method 'getUsersById'");
    }

    public Object refreshToken(ReqRes req) {
        throw new UnsupportedOperationException("Unimplemented method 'refreshToken'");
    }
}
