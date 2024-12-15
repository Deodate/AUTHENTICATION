package com.phegondev.usersmanagementsystem.Controller;

import com.phegondev.usersmanagementsystem.DTO.ReqRes;
import com.phegondev.usersmanagementsystem.Model.UsersAccounts;
import com.phegondev.usersmanagementsystem.service.UsersManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserManagementController {
    @Autowired
    private UsersManagementService usersManagementService;

    @PostMapping("/auth/register")
    public ResponseEntity<ReqRes> register(@RequestBody ReqRes reg) {
        return ResponseEntity.ok(usersManagementService.register(reg));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ReqRes> login(@RequestBody ReqRes req) {
        return ResponseEntity.ok(usersManagementService.login(req));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<Object> refreshToken(@RequestBody ReqRes req) {
        return ResponseEntity.ok(usersManagementService.refreshToken(req));
    }

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<ReqRes> getAllUsers() {
        return ResponseEntity.ok(usersManagementService.getAllUsers());
    }

    @GetMapping("/admin/get-users/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Integer userId) {
        return ResponseEntity.ok(usersManagementService.getUsersById(userId));
    }

    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable Integer userId, @RequestBody UsersAccounts reqres) {
        return ResponseEntity.ok(usersManagementService.updateUser(userId, reqres));
    }

    @GetMapping("/headmasterteacher/get-profile")
    public ResponseEntity<ReqRes> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ReqRes response = usersManagementService.getMyInfo(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/header/delete/{userId}")
    public ResponseEntity<ReqRes> deleteUser(@PathVariable Integer userId) {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String position = authentication.getAuthorities().stream()
            .map(Object::toString)
            .findFirst()
            .orElse("");
        return ResponseEntity.ok(usersManagementService.deleteUser(userId, position));
    }

    @PostMapping("/auth/logins")
    public ResponseEntity<ReqRes> initiateLogin(@RequestBody ReqRes req) {
        return ResponseEntity.ok(usersManagementService.initiateLogin(req));
    }

    // @PostMapping("/auth/verify-login-otp")
    // public ResponseEntity<ReqRes> verifyLoginOtp(@RequestBody ReqRes req) {
    //     return ResponseEntity.ok(usersManagementService.verifyLoginOtp(req));
    // }
}