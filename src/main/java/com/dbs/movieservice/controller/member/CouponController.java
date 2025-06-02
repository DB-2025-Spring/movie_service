package com.dbs.movieservice.controller.member;

import com.dbs.movieservice.controller.dto.IssuedCouponResponse;
import com.dbs.movieservice.service.member.IssueCouponService;
import com.dbs.movieservice.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "쿠폰 API", description = "사용자 쿠폰 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class CouponController {

    private final IssueCouponService issueCouponService;

    @GetMapping("/my-coupons")
    @Operation(summary = "내 쿠폰 목록 조회", description = "현재 로그인한 사용자에게 발급된 모든 쿠폰 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "쿠폰 목록 조회 성공",
                content = @Content(schema = @Schema(implementation = IssuedCouponResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> getMyCoupons() {
        String customerInputId = SecurityUtils.getCurrentCustomerInputId();
        
        List<IssuedCouponResponse> coupons = issueCouponService.getIssuedCouponsByCustomerInputId(customerInputId);
        
        log.info("Retrieved {} coupons for user: {}", coupons.size(), customerInputId);
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/my-coupons/usable")
    @Operation(summary = "사용 가능한 쿠폰 목록 조회", description = "현재 로그인한 사용자의 사용 가능한 쿠폰 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용 가능한 쿠폰 목록 조회 성공",
                content = @Content(schema = @Schema(implementation = IssuedCouponResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> getMyUsableCoupons() {
        String customerInputId = SecurityUtils.getCurrentCustomerInputId();
        
        List<IssuedCouponResponse> usableCoupons = issueCouponService.getUsableCouponsByCustomerInputId(customerInputId);
        
        log.info("Retrieved {} usable coupons for user: {}", usableCoupons.size(), customerInputId);
        return ResponseEntity.ok(usableCoupons);
    }

    @PostMapping("/issue/{couponId}")
    @Operation(summary = "쿠폰 발급", description = "현재 로그인한 사용자에게 특정 쿠폰을 발급합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "쿠폰 발급 성공",
                content = @Content(schema = @Schema(implementation = IssuedCouponResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (이미 발급된 쿠폰 등)"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> issueCoupon(
            @Parameter(description = "발급할 쿠폰 ID", required = true)
            @PathVariable Long couponId) {
        String customerInputId = SecurityUtils.getCurrentCustomerInputId();
        
        IssuedCouponResponse issuedCoupon = issueCouponService.issueCouponToCustomer(customerInputId, couponId);
        
        log.info("Successfully issued coupon {} to user: {}", couponId, customerInputId);
        return ResponseEntity.status(HttpStatus.CREATED).body(issuedCoupon);
    }

    @GetMapping("/has-coupon/{couponId}")
    @Operation(summary = "쿠폰 보유 여부 확인", description = "현재 로그인한 사용자가 특정 쿠폰을 보유하고 있는지 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "쿠폰 보유 여부 확인 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> hasCoupon(
            @Parameter(description = "확인할 쿠폰 ID", required = true)
            @PathVariable Long couponId) {
        String customerInputId = SecurityUtils.getCurrentCustomerInputId();
        
        boolean hasCoupon = issueCouponService.hasCoupon(customerInputId, couponId);
        
        return ResponseEntity.ok(new HasCouponResponse(hasCoupon, couponId, customerInputId));
    }

    /**
     * 쿠폰 보유 여부 응답 DTO
     */
    @Schema(description = "쿠폰 보유 여부 응답")
    public static class HasCouponResponse {
        @Schema(description = "쿠폰 보유 여부", example = "true")
        private boolean hasCoupon;
        
        @Schema(description = "쿠폰 ID", example = "1")
        private Long couponId;
        
        @Schema(description = "사용자 ID", example = "testuser123")
        private String customerInputId;

        public HasCouponResponse(boolean hasCoupon, Long couponId, String customerInputId) {
            this.hasCoupon = hasCoupon;
            this.couponId = couponId;
            this.customerInputId = customerInputId;
        }

        // Getters
        public boolean isHasCoupon() { return hasCoupon; }
        public Long getCouponId() { return couponId; }
        public String getCustomerInputId() { return customerInputId; }
    }
} 