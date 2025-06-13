package com.example.halu_be.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "🚀 Halu BE is running! Welcome to your monolithic backend.";
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
