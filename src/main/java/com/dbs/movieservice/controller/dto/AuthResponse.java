package com.dbs.movieservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    
    private String token;
    private String type = "Bearer";
    private String customerInputId;
    private String authority;
    
    public AuthResponse(String token, String customerInputId, String authority) {
        this.token = token;
        this.customerInputId = customerInputId;
        this.authority = authority;
    }
} 