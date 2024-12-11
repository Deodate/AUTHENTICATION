package com.phegondev.usersmanagementsystem.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.phegondev.usersmanagementsystem.Model.UsersAccounts;

import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqRes {

    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String fullName;
    private String phone;
    private String nationality;
    private String role;
    private String email;
    private String password;
    private UsersAccounts ourUsers;
    private List<UsersAccounts> ourUsersList;

}
