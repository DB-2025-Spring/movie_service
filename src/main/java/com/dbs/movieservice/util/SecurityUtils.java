package com.dbs.movieservice.util;

import com.dbs.movieservice.domain.member.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtils {

    /**
     * 현재 로그인한 사용자의 customerInputId를 가져옵니다.
     * 
     * @return customerInputId
     * @throws RuntimeException 인증되지 않은 사용자인 경우
     */
    public static String getCurrentCustomerInputId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        
        // anonymous 사용자 체크
        if ("anonymousUser".equals(authentication.getName())) {
            throw new RuntimeException("Anonymous user is not allowed");
        }
        
        return authentication.getName();
    }

    /**
     * 현재 로그인한 사용자의 권한을 가져옵니다.
     * 
     * @return Role 사용자 권한 (ROLE_ADMIN, ROLE_MEMBER, ROLE_GUEST)
     * @throws RuntimeException 인증되지 않았거나 권한이 없는 경우
     */
    public static Role getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        
        String authority = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new RuntimeException("No authority found"));
        
        // authority는 "ROLE_ADMIN", "ROLE_MEMBER", "ROLE_GUEST" 형식
        try {
            return Role.valueOf(authority);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown authority: {}, defaulting to ROLE_GUEST", authority);
            return Role.ROLE_GUEST;
        }
    }

    /**
     * 현재 인증된 사용자인지 확인합니다.
     * 
     * @return boolean 인증 여부
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        return authentication != null && 
               authentication.isAuthenticated() && 
               !"anonymousUser".equals(authentication.getName());
    }
} 