package com.dbs.movieservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class temp {
    @GetMapping("/test")
    public String test() {
        return "Swagger is working!";
    }
}
