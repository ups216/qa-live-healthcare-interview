package com.leansofx.qaservicequestion.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ServiceHomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
                "service", "qa-service-question",
                "status", "running",
                "message", "Question service is running on port 8081.",
                "apiExamples", new String[]{
                        "/api/questions",
                        "/api/questions?doctorId=doc001",
                        "/api/questions?patientId=1",
                        "/api/questions/summary"
                }
        );
    }
}
