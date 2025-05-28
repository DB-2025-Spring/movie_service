package com.dbs.movieservice.controller.member;

import com.dbs.movieservice.config.AdminProperties;
import com.dbs.movieservice.config.JwtUtils;
import com.dbs.movieservice.controller.dto.AuthRequest;
import com.dbs.movieservice.controller.dto.AuthResponse;
import com.dbs.movieservice.controller.dto.SignupRequest;
import com.dbs.movieservice.domain.member.Role;
import com.dbs.movieservice.service.member.CustomerService;
import com.dbs.movieservice.service.member.BirthdayCouponService;
import com.dbs.movieservice.service.member.SignupCouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
@Tag(name = "인증 API", description = "사용자 인증 및 회원가입 관련 API")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomerService customerService;
    private final JwtUtils jwtUtils;
    private final AdminProperties adminProperties;
    private final PasswordEncoder passwordEncoder;
    private final BirthdayCouponService birthdayCouponService;
    private final SignupCouponService signupCouponService;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 또는 관리자 로그인을 처리합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "로그인 실패 - 잘못된 아이디 또는 비밀번호",
                content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 - 필수 필드 누락 또는 형식 오류",
                content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> authenticateCustomer(@Valid @RequestBody AuthRequest loginRequest, 
                                                 HttpServletRequest request) {
        
        try {
            // 1. 관리자 계정 확인
            if (isAdminAccount(loginRequest)) {
                return handleAdminLogin(loginRequest, request);
            }
            
            // 2. 일반 사용자 로그인 처리
            return handleUserLogin(loginRequest);
        } catch (BadCredentialsException e) {
            log.warn("Login failed - Invalid credentials for user: {} from IP: {}", 
                    loginRequest.getCustomerInputId(), getClientIP(request));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        } catch (AuthenticationException e) {
            log.warn("Login failed - Authentication error for user: {} from IP: {}", 
                    loginRequest.getCustomerInputId(), getClientIP(request));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication failed");
        } catch (Exception e) {
            log.error("Login failed - Unexpected error for user: {} from IP: {}", 
                    loginRequest.getCustomerInputId(), getClientIP(request), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error");
        }
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid administrator credentials");
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

        // 로그인 성공 후 생일 쿠폰 발급 체크
        try {
            boolean birthdayCouponIssued = birthdayCouponService.issueBirthdayCouponIfEligible(loginRequest.getCustomerInputId());
            if (birthdayCouponIssued) {
                log.info("Birthday coupon issued during login for user: {}", loginRequest.getCustomerInputId());
            }
        } catch (Exception e) {
            log.warn("Failed to check/issue birthday coupon during login for user: {}", 
                    loginRequest.getCustomerInputId(), e);
            // 생일 쿠폰 발급 실패는 로그인 실패로 처리하지 않음
        }

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
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "회원가입 성공",
                content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "409", description = "회원가입 실패 - 아이디 중복",
                content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 - 필수 필드 누락 또는 형식 오류",
                content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            // DTO를 서비스 계층에 전달하여 Customer 생성 및 저장 로직을 처리
            customerService.registerCustomer(signupRequest);
            
            // 회원가입 성공 후 신규가입쿠폰 발급
            try {
                boolean signupCouponIssued = signupCouponService.issueSignupCoupon(signupRequest.getCustomerInputId());
                if (signupCouponIssued) {
                    log.info("Signup coupon issued for new user: {}", signupRequest.getCustomerInputId());
                }
            } catch (Exception e) {
                log.warn("Failed to issue signup coupon for new user: {}", 
                        signupRequest.getCustomerInputId(), e);
                // 신규가입쿠폰 발급 실패는 회원가입 실패로 처리하지 않음
            }
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("User registered successfully!");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Username is already taken")) {
                log.warn("Signup failed - Username already exists: {}", signupRequest.getCustomerInputId());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Username is already taken");
            }
            log.error("Signup failed - Unexpected error for user: {}", signupRequest.getCustomerInputId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed due to server error");
        } catch (Exception e) {
            log.error("Signup failed - Unexpected error for user: {}", signupRequest.getCustomerInputId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed due to server error");
        }
    }
    
    @GetMapping("/check-username")
    @Operation(summary = "아이디 중복 확인", description = "회원가입 시 아이디 중복 여부를 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "중복 확인 완료",
                content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터",
                content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> checkUsername(
            @Parameter(description = "확인할 사용자 ID", example = "testuser123", required = true) 
            @RequestParam String customerInputId) {
        
        try {
            if (customerInputId == null || customerInputId.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"Customer ID is required\"}");
            }
            
            boolean isDuplicate = customerService.checkDuplicateCustomerId(customerInputId);
            
            if (isDuplicate) {
                return ResponseEntity.ok()
                        .body("{\"available\": false, \"message\": \"Username is already taken\"}");
            } else {
                return ResponseEntity.ok()
                        .body("{\"available\": true, \"message\": \"Username is available\"}");
            }
        } catch (Exception e) {
            log.error("Username check failed for: {}", customerInputId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Internal server error\"}");
        }
    }
} 
