package com.dbs.movieservice.domain.member;

public enum Role {
    ROLE_ADMIN("A"),
    ROLE_MEMBER("M"),
    ROLE_GUEST("G");
    
    private final String code;
    
    Role(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
    
    public static Role fromCode(String code) {
        for (Role role : Role.values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return ROLE_GUEST; // Default role
    }
} 
