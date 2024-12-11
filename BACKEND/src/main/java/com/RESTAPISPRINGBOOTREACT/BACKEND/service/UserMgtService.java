package com.RESTAPISPRINGBOOTREACT.BACKEND.service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.RESTAPISPRINGBOOTREACT.BACKEND.DTO.ReqResponse;
import com.RESTAPISPRINGBOOTREACT.BACKEND.entity.Userstbl;
import com.RESTAPISPRINGBOOTREACT.BACKEND.repository.UsersRepository;

@Service
public class UserMgtService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

     @Autowired
    public UserMgtService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public ReqResponse register(ReqResponse registrationRequest) {
        ReqResponse resp = new ReqResponse();

        try {
            // Create a new Userstbl object and set its properties from the request
            Userstbl userstbl = new Userstbl();
            userstbl.setEmail(registrationRequest.getEmail());
            userstbl.setFullName(registrationRequest.getFullName());
            userstbl.setPhone(registrationRequest.getPhone());
            userstbl.setPassword(passwordEncoder.encode(registrationRequest.getPassword())); // Encode the password
            userstbl.setNationality(registrationRequest.getNationality());
            userstbl.setPosition(registrationRequest.getPosition()); // Position will define the role

            // Save the user to the database
            Userstbl userstblResult = usersRepository.save(userstbl);

            if (userstblResult.getId() != null) { // Use non-null check for UUID
                resp.setUserstbl(userstblResult);
                resp.setMessage("User registration successful");
                resp.setStatusCode(200); // OK
            }

        } catch (Exception e) {
            resp.setStatusCode(500); // Internal Server Error
            resp.setError("Registration failed: " + e.getMessage());
        }

        return resp;
    }

    public ReqResponse login(ReqResponse loginRequest) {
        ReqResponse reqResponse = new ReqResponse();

        try {
            // Authenticate the user credentials
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            // Fetch the user from the database
            Userstbl user = usersRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + loginRequest.getEmail()));

            // Generate JWT tokens
            String jwt = jwtUtils.generateToken(user);
            String refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

            // Prepare the response
            reqResponse.setStatusCode(200);
            reqResponse.setToken(jwt);
            reqResponse.setRefreshToken(refreshToken);
            reqResponse.setExpirationTime("24Hrs");
            reqResponse.setMessage("Successfully Logged In");
            reqResponse.setUserstbl(user);

        } catch (Exception e) {
            reqResponse.setStatusCode(401); // Unauthorized
            reqResponse.setMessage("Login failed: " + e.getMessage());
        }

        return reqResponse;
    }

    public ReqResponse getAllUsers() {
        ReqResponse reqResponse = new ReqResponse();

        try {
            // Fetch all users from the database
            List<Userstbl> result = usersRepository.findAll();

            if (!result.isEmpty()) {
                reqResponse.setUserstblList(result);
                reqResponse.setStatusCode(200);
                reqResponse.setMessage("Successful");
            } else {
                reqResponse.setStatusCode(404);
                reqResponse.setMessage("No users found");
            }
            return reqResponse;

        } catch (Exception e) {
            reqResponse.setStatusCode(500); // Internal Server Error
            reqResponse.setMessage("Error occurred: " + e.getMessage());
            return reqResponse;
        }

    }

    public ReqResponse getUsersByPhone(String phone) {
        ReqResponse reqResponse = new ReqResponse();

        try {
            // Fetch user by phone number or throw a custom exception if not found
            Userstbl userByPhone = usersRepository.findByPhone(phone)
                    .orElseThrow(() -> new RuntimeException("User with phone number '" + phone + "' not found"));

            reqResponse.setUserstbl(userByPhone);
            reqResponse.setStatusCode(200);
            reqResponse.setMessage("User with phone '" + phone + "' found successfully");

        } catch (RuntimeException e) {
            reqResponse.setStatusCode(404); // Not Found
            reqResponse.setMessage(e.getMessage());
        } catch (Exception e) {
            reqResponse.setStatusCode(500); // Internal Server Error
            reqResponse.setMessage("Error occurred: " + e.getMessage());
        }

        return reqResponse;
    }

    public ReqResponse deleteUser(UUID userId) {
        ReqResponse reqResponse = new ReqResponse();

        try {
            // Check if the user exists by ID
            Userstbl user = usersRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User with ID '" + userId + "' not found"));

            // Delete the user
            usersRepository.delete(user);

            reqResponse.setStatusCode(200);
            reqResponse.setMessage("User with ID '" + userId + "' deleted successfully");

        } catch (RuntimeException e) {
            reqResponse.setStatusCode(404); // Not Found
            reqResponse.setMessage(e.getMessage());
        } catch (Exception e) {
            reqResponse.setStatusCode(500); // Internal Server Error
            reqResponse.setMessage("Error occurred: " + e.getMessage());
        }

        return reqResponse;
    }

    public ReqResponse updateUser(UUID userId, Userstbl updateRequest) {
        ReqResponse reqResponse = new ReqResponse();

        try {
            Optional<Userstbl> userOptional = usersRepository.findById(userId);

            if (userOptional.isPresent()) {
                Userstbl existingUser = userOptional.get();

                // Update fields with new values from the updateRequest object
                existingUser.setEmail(updateRequest.getEmail()); // Update email
                existingUser.setFullName(updateRequest.getFullName()); // Update full name
                existingUser.setNationality(updateRequest.getNationality()); // Update nationality
                existingUser.setPosition(updateRequest.getPosition()); // Update position
                existingUser.setPhone(updateRequest.getPhone()); // Update phone number

                // Update password only if it's provided (and not empty)
                if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(updateRequest.getPassword())); // Update password
                }

                // Save the updated user to the database
                Userstbl savedUser = usersRepository.save(existingUser);
                reqResponse.setUserstbl(savedUser);
                reqResponse.setMessage("User updated successfully!");
            } else {
                reqResponse.setStatusCode(400); // Bad request, user not found
                reqResponse.setMessage("User not found for update");
            }

        } catch (Exception e) {
            reqResponse.setStatusCode(500); // Internal Server Error
            reqResponse.setMessage("Error occurred: " + e.getMessage());
        }

        return reqResponse;
    }

    // Method to activate a user's account (by Admin)
    public ReqResponse activateUser(UUID userId) {
        ReqResponse reqResponse = new ReqResponse();

        try {
            // Find the user by ID
            Userstbl user = usersRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            // Check if the user is a teacher and not already active
            if (!user.isActive()) {
                user.setActive(true); // Set the user account to active
                usersRepository.save(user); // Save the updated user

                reqResponse.setStatusCode(200);
                reqResponse.setMessage("User with ID '" + userId + "' has been successfully activated");
            } else {
                reqResponse.setStatusCode(400); // Bad request if user is already active
                reqResponse.setMessage("User account is already active");
            }
        } catch (RuntimeException e) {
            reqResponse.setStatusCode(404); // Not Found
            reqResponse.setMessage(e.getMessage());
        } catch (Exception e) {
            reqResponse.setStatusCode(500); // Internal Server Error
            reqResponse.setMessage("Error occurred: " + e.getMessage());
        }

        return reqResponse;
    }

   public ReqResponse getAccountDetails(String emailOrPhone) {
    ReqResponse reqResponse = new ReqResponse();

    try {
        Optional<Userstbl> userOptional = Optional.empty();
        
        // Check if the input is an email or a phone number
        if (emailOrPhone.contains("@")) { // Simple check to identify email
            userOptional = usersRepository.findByEmail(emailOrPhone); // Fetch by email
        } else {
            userOptional = usersRepository.findByPhone(emailOrPhone); // Fetch by phone number
        }

        if (userOptional.isPresent()) {
            reqResponse.setUserstbl(userOptional.get());
            reqResponse.setStatusCode(200);
            reqResponse.setMessage("User details fetched successfully!");
        } else {
            reqResponse.setStatusCode(404);
            reqResponse.setMessage("User not found!");
        }
    } catch (Exception e) {
        reqResponse.setStatusCode(500);
        reqResponse.setMessage("Error occurred while getting user info: " + e.getMessage());
    }

    return reqResponse;
}


}
