package com.dbs.movieservice.controller.member;

import com.dbs.movieservice.config.JwtUtils;
import com.dbs.movieservice.controller.dto.AuthRequest;
import com.dbs.movieservice.controller.dto.AuthResponse;
import com.dbs.movieservice.controller.dto.SignupRequest;
import com.dbs.movieservice.domain.member.Role;
import com.dbs.movieservice.service.member.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomerService customerService;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateCustomer(@Valid @RequestBody AuthRequest loginRequest) {
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
