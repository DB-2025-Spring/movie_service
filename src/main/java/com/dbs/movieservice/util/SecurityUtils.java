package com.dbs.movieservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * 현재 로그인한 사용자의 권한 목록을 가져옵니다.
     * 
     * @return List<String> 권한 목록
     */
    public static List<String> getCurrentUserAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return List.of();
        }
        
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
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