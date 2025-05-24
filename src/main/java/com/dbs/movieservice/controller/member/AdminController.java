package com.dbs.movieservice.controller.member;

import com.dbs.movieservice.controller.dto.ClientLevelRequest;
import com.dbs.movieservice.domain.member.ClientLevel;
import com.dbs.movieservice.service.member.ClientLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final ClientLevelService clientLevelService;

    @GetMapping("/movies")
    public String getMoviesForAdmin() {
        return "Admin can manage all movies here";
    }
    
    @PostMapping("/movies")
    public String addMovie() {
        return "Admin can add new movies";
    }
    
    @GetMapping("/theaters")
    public String getTheaters() {
        return "Admin can manage theaters";
    }
    
    @GetMapping("/schedules")
    public String getSchedules() {
        return "Admin can manage screening schedules";
    }
    
    @GetMapping("/actors")
    public String getActors() {
        return "Admin can manage actors";
    }
    
    @GetMapping("/dashboard")
    public String getDashboard() {
        return "Admin dashboard with analytics";
    }
    
    /**
     * 모든 고객 등급 조회
     */
    @GetMapping("/client-levels")
    public ResponseEntity<List<ClientLevel>> getAllClientLevels() {
        return ResponseEntity.ok(clientLevelService.findAllLevels());
    }
    
    /**
     * 특정 고객 등급 조회
     */
    @GetMapping("/client-levels/{levelId}")
    public ResponseEntity<ClientLevel> getClientLevel(@PathVariable Integer levelId) {
        return clientLevelService.findLevelById(levelId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 고객 등급 수정
     */
    @PutMapping("/client-levels/{levelId}")
    public ResponseEntity<ClientLevel> updateClientLevel(
            @PathVariable Integer levelId,
            @RequestBody ClientLevelRequest request) {
        ClientLevel updatedLevel = clientLevelService.updateLevel(
                levelId,
                request.getLevelName(),
                request.getRewardRate()
        );
        return ResponseEntity.ok(updatedLevel);
    }
} 
