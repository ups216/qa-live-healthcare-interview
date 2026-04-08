package com.leansofx.qaserviceuser.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * CORS 测试用接口，供前端或 curl 验证跨域配置是否生效。
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/cors")
    public Map<String, Object> testCors() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "CORS configuration is working!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("service", "qa-service-user");
        return response;
    }

    @PostMapping("/cors")
    public Map<String, Object> testCorsPost(@RequestBody(required = false) Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "POST request with CORS is working!");
        response.put("receivedData", data);
        response.put("timestamp", System.currentTimeMillis());
        response.put("service", "qa-service-user");
        return response;
    }

    @RequestMapping(value = "/cors", method = RequestMethod.OPTIONS)
    public void handleOptions() {
        // Spring Boot 会自动处理 OPTIONS 请求
    }
}