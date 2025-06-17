package com.dbs.movieservice.controller.member;

import com.dbs.movieservice.controller.dto.CustomerProfileResponse;
import com.dbs.movieservice.controller.dto.CustomerResponse;
import com.dbs.movieservice.controller.dto.CustomerUpdateRequest;
import com.dbs.movieservice.domain.member.Customer;
import com.dbs.movieservice.service.member.CustomerProfileService;
import com.dbs.movieservice.service.member.CustomerService;
import com.dbs.movieservice.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "고객 프로필 API", description = "고객 프로필 조회 및 수정 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final CustomerProfileService customerProfileService;
    @GetMapping("/profile")
    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 사용자의 프로필 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "프로필 조회 성공",
                content = @Content(schema = @Schema(implementation = CustomerProfileResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<CustomerProfileResponse> getMyProfile() {
        String customerInputId = SecurityUtils.getCurrentCustomerInputId();
        
        CustomerProfileResponse profile = customerProfileService.getCustomerProfile(customerInputId);
        
        log.info("Retrieved profile for user: {}", customerInputId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @Operation(summary = "내 프로필 수정", description = "현재 로그인한 사용자의 프로필 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "프로필 수정 성공",
                content = @Content(schema = @Schema(implementation = CustomerProfileResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<CustomerProfileResponse> updateMyProfile(
            @Valid @RequestBody CustomerUpdateRequest request) {
        String customerInputId = SecurityUtils.getCurrentCustomerInputId();
        
        CustomerProfileResponse updatedProfile = customerProfileService.updateCustomerProfile(customerInputId, request);
        
        log.info("Updated profile for user: {}", customerInputId);
        return ResponseEntity.ok(updatedProfile);
    }
} 