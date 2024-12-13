package com.phegondev.usersmanagementsystem.DTO;

import lombok.Builder;
import lombok.Data;


@Builder
public record MailBody(String to, String subject, String text) {
    
}
