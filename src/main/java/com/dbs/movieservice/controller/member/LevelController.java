package com.dbs.movieservice.controller.member;

import com.dbs.movieservice.controller.dto.LevelUpdateResponse;
import com.dbs.movieservice.service.member.LevelUpService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/level")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "등급 관리 API", description = "사용자 등급 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class LevelController {

    private final LevelUpService levelUpService;

    @PostMapping("/update")
    @Operation(summary = "등급 업데이트", description = "티켓 구매 수량에 따라 사용자 등급을 업데이트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "등급 업데이트 처리 완료",
                content = @Content(schema = @Schema(implementation = LevelUpdateResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> updateLevel(
            @Parameter(description = "구매한 티켓 수량", required = true, example = "5")
            @RequestParam int ticketCount) {
        
        String customerInputId = SecurityUtils.getCurrentCustomerInputId();
        
        log.info("Processing level update for user: {} with ticket count: {}", 
                customerInputId, ticketCount);
        
        boolean levelUpOccurred = levelUpService.processLevelUp(customerInputId, ticketCount);
        
        return ResponseEntity.ok(LevelUpdateResponse.builder()
                .levelUpOccurred(levelUpOccurred)
                .customerInputId(customerInputId)
                .ticketCount(ticketCount)
                .message(levelUpOccurred ? "등급이 업그레이드되었습니다! 축하 쿠폰이 발급되었습니다." : "등급 업데이트가 필요하지 않습니다.")
                .build());
    }

    @PostMapping("/update/{customerInputId}")
    @Operation(summary = "특정 사용자 등급 업데이트 (관리자용)", 
               description = "관리자가 특정 사용자의 등급을 업데이트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "등급 업데이트 처리 완료"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "권한 부족"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<?> updateLevelForUser(
            @Parameter(description = "대상 사용자 ID", required = true)
            @PathVariable String customerInputId,
            @Parameter(description = "구매한 티켓 수량", required = true, example = "10")
            @RequestParam int ticketCount) {
        
        // 관리자 권한 체크는 Spring Security에서 처리
        log.info("Admin processing level update for user: {} with ticket count: {}", 
                customerInputId, ticketCount);
        
        boolean levelUpOccurred = levelUpService.processLevelUp(customerInputId, ticketCount);
        
        return ResponseEntity.ok(LevelUpdateResponse.builder()
                .levelUpOccurred(levelUpOccurred)
                .customerInputId(customerInputId)
                .ticketCount(ticketCount)
                .message(levelUpOccurred ? "등급이 업그레이드되었습니다! 축하 쿠폰이 발급되었습니다." : "등급 업데이트가 필요하지 않습니다.")
                .build());
    }
} 