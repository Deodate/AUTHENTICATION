package com.phegondev.usersmanagementsystem.utils;

public record ChangePassword(String password, String repeatPassword) {
    
      public boolean isValid() {
        // Check if the passwords match
        return password.equals(repeatPassword);
    }
}
