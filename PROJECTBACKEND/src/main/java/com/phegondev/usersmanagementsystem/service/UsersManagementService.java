package com.phegondev.usersmanagementsystem.service;

import com.phegondev.usersmanagementsystem.DTO.ReqRes;
import com.phegondev.usersmanagementsystem.Model.UsersAccounts;
import com.phegondev.usersmanagementsystem.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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

            resp.setOurUsers(savedUser);
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

            // Check if the email is being changed and if the new email already exists
            if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
                Optional<UsersAccounts> existingEmailUser = usersRepo.findByEmail(updatedUser.getEmail());
                if (existingEmailUser.isPresent()) {
                    reqRes.setStatusCode(400);
                    reqRes.setMessage("Email is already in use.");
                    return reqRes;
                }
            }

            // Update user fields
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

            reqRes.setOurUsers(savedUser);
            reqRes.setStatusCode(200);
            reqRes.setMessage("User updated successfully");

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
        Optional<UsersAccounts> user = usersRepo.findByEmail(email);

        if (user.isPresent()) {
            resp.setOurUsers(user.get());
            resp.setMessage("User found");
            resp.setStatusCode(200);
        } else {
            resp.setMessage("User not found");
            resp.setStatusCode(404);
            resp.setError("No such user exists");
        }

        return resp;
    }

    // UsersManagementService.java

    public ReqRes getAllUsers() {
        ReqRes resp = new ReqRes();

        try {
            // Fetch all users from the repository
            List<UsersAccounts> allUsers = usersRepo.findAll();

            if (allUsers.isEmpty()) {
                resp.setStatusCode(404);
                resp.setMessage("No users found");
            } else {
                resp.setStatusCode(200);
                resp.setMessage("Users retrieved successfully");
                resp.setOurUsersList(allUsers); // Assuming you have a setOurUsers method to set the list of users
            }

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setMessage("Error retrieving users: " + e.getMessage());
        }

        return resp;
    }
    

    public ReqRes deleteUser(Integer userId) {
        ReqRes resp = new ReqRes();

        try {
            // Check if the user exists
            Optional<UsersAccounts> userOptional = usersRepo.findById(userId);

            if (userOptional.isPresent()) {
                // Delete the user
                usersRepo.delete(userOptional.get());

                // Set success response
                resp.setStatusCode(200);
                resp.setMessage("User deleted successfully");
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

    public Object getUsersById(Integer userId) {
        throw new UnsupportedOperationException("Unimplemented method 'getUsersById'");
    }

    public Object refreshToken(ReqRes req) {
        throw new UnsupportedOperationException("Unimplemented method 'refreshToken'");
    }
}
