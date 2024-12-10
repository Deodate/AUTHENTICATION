package com.RESTAPISPRINGBOOTREACT.BACKEND.service;

import org.springframework.beans.factory.annotation.Autowired;
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
                resp.setUserstbl((userstblResult)); // Ensure ReqResponse has a setter for Userstbl
                resp.setMessage("User registration successful");
                resp.setStatusCode(200); // OK
            }

        } catch (Exception e) {
            resp.setStatusCode(500); // Internal Server Error
            resp.setError("Registration failed: " + e.getMessage());
        }

        return resp;
    }
}
