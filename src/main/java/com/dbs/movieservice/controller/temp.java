package com.dbs.movieservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class temp {
    @GetMapping("/test")
    public String test() {
        return "Swagger is working!";
    }
}
