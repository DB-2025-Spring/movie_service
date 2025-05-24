package com.dbs.movieservice.controller.member;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
public class MemberController {

    @GetMapping
    public String getMyBookings() {
        return "List of current user's bookings";
    }
    
    @PostMapping
    public String createBooking() {
        return "Create a new booking";
    }
    
    @GetMapping("/history")
    public String getBookingHistory() {
        return "View booking history";
    }
    
    @GetMapping("/preferences")
    public String getPreferences() {
        return "View and update movie preferences";
    }
} 
