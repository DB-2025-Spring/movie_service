package com.dbs.movieservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class temp {
    
    @GetMapping("/test")
    @Operation(summary = "테스트 API", description = "Swagger 연동 테스트를 위한 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 응답을 반환했습니다."),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.")
    })
    public String test() {
        return "Swagger is working!";
    }
}
