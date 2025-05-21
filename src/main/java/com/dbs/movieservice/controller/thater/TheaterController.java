package com.dbs.movieservice.controller.thater;

import com.dbs.movieservice.domain.theater.Theater;
import com.dbs.movieservice.service.theater.TheaterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/theater")
public class TheaterController {
    private final TheaterService theaterService;

    public TheaterController(TheaterService theaterService) {
        this.theaterService = theaterService;
    }

    @GetMapping("/test-create")
    public Theater createTestTheater() {
        Theater theater = new Theater();
        theater.setTheater_name("테스트관");
        theater.setTotal_seats(123);

        return theaterService.createTheater(theater);
    }
}
