package com.dbs.movieservice.controller.member;

import com.dbs.movieservice.config.AdminProperties;
import com.dbs.movieservice.config.JwtUtils;
import com.dbs.movieservice.controller.dto.AuthRequest;
import com.dbs.movieservice.controller.dto.AuthResponse;
import com.dbs.movieservice.controller.dto.SignupRequest;
import com.dbs.movieservice.domain.member.Role;
import com.dbs.movieservice.service.member.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomerService customerService;
    private final JwtUtils jwtUtils;
    private final AdminProperties adminProperties;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateCustomer(@Valid @RequestBody AuthRequest loginRequest, 
                                                 HttpServletRequest request) {
        
        // 1. 관리자 계정 확인
        if (isAdminAccount(loginRequest)) {
            return handleAdminLogin(loginRequest, request);
        }
        
        // 2. 일반 사용자 로그인 처리
        return handleUserLogin(loginRequest);
    }
    
    /**
     * 관리자 계정인지 확인
     */
    private boolean isAdminAccount(AuthRequest loginRequest) {
        return adminProperties.isEnabled() && 
               adminProperties.getUsername().equals(loginRequest.getCustomerInputId());
    }
    
    /**
     * 관리자 로그인 처리
     */
    private ResponseEntity<?> handleAdminLogin(AuthRequest loginRequest, HttpServletRequest request) {
        // 관리자 비밀번호 검증 (단순 문자열 비교)
        if (!adminProperties.getPassword().equals(loginRequest.getCustomerPw())) {
            log.warn("Admin login failed - Invalid password for user: {} from IP: {}", 
                    loginRequest.getCustomerInputId(), getClientIP(request));
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        
        // 관리자 인증 객체 생성
        User adminUser = new User(
            adminProperties.getUsername(),
            passwordEncoder.encode(adminProperties.getPassword()),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(
            adminUser, null, adminUser.getAuthorities()
        );
        
        SecurityContextHolder.getContext().setAuthentication(adminAuth);
        
        // JWT 토큰 생성
        String jwt = jwtUtils.generateToken(adminAuth);
        
        // 관리자 로그인 로그
        log.info("Admin login successful for user: {} from IP: {}", 
                loginRequest.getCustomerInputId(), getClientIP(request));
        
        return ResponseEntity.ok(new AuthResponse(jwt, adminUser.getUsername(), "ADMIN"));
    }
    
    /**
     * 일반 사용자 로그인 처리
     */
    private ResponseEntity<?> handleUserLogin(AuthRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getCustomerInputId(),
                        loginRequest.getCustomerPw()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 로그인 성공 시 GUEST 권한을 가진 사용자를 MEMBER로 업그레이드 (관리자는 제외)
        customerService.upgradeGuestToMember(loginRequest.getCustomerInputId());
        
        // 권한 업그레이드 후 새 인증 객체 생성 필요
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getCustomerInputId(),
                        loginRequest.getCustomerPw()
                )
        );
        
        String jwt = jwtUtils.generateToken(authentication);

        org.springframework.security.core.userdetails.User userDetails = 
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        
        String authority = userDetails.getAuthorities().stream()
                .findFirst()
                .map(auth -> {
                    String role = auth.getAuthority();
                    return Role.valueOf(role).getCode();
                })
                .orElse(Role.ROLE_GUEST.getCode());

        return ResponseEntity.ok(new AuthResponse(jwt, userDetails.getUsername(), authority));
    }
    
    /**
     * 클라이언트 IP 주소 추출
     */
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody SignupRequest signupRequest) {
        // DTO를 서비스 계층에 전달하여 Customer 생성 및 저장 로직을 처리
        customerService.registerCustomer(signupRequest);
        return ResponseEntity.ok("User registered successfully!");
    }
    
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String customerInputId) {
        boolean isDuplicate = customerService.checkDuplicateCustomerId(customerInputId);
        
        if (isDuplicate) {
            return ResponseEntity.ok().body("{\"available\": false, \"message\": \"Username is already taken\"}");
        } else {
            return ResponseEntity.ok().body("{\"available\": true, \"message\": \"Username is available\"}");
        }
    }
} 
