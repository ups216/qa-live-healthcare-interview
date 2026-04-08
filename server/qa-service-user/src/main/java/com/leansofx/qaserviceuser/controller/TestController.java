package com.leansofx.qaserviceuser.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/api/ping")
    public Map<String, Object> ping() {
        return Collections.singletonMap("message", "ok");
    }
}