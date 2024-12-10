package com.RESTAPISPRINGBOOTREACT.BACKEND.DTO;

import java.util.List;

import com.RESTAPISPRINGBOOTREACT.BACKEND.entity.Userstbl;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReqResponse {

    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expiration;
    private String fullName;
    private String nationality;
    private String position;
    private String phone;
    private String email;
    private String password;
    private Userstbl userstbl;
    private List<Userstbl> userstblList;

    
}
