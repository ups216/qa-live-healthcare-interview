package com.leansofx.qaserviceuser.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class DatabaseTestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db-connection")
    public Map<String, Object> testDatabaseConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 测试数据库连接
            Integer count = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            
            if (count != null && count == 1) {
                result.put("status", "success");
                result.put("message", "数据库连接成功");
                result.put("connected", true);
            } else {
                result.put("status", "error");
                result.put("message", "数据库连接测试失败");
                result.put("connected", false);
            }
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "数据库连接失败: " + e.getMessage());
            result.put("connected", false);
        }
        
        return result;
    }

    @GetMapping("/tables")
    public Map<String, Object> listTables() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 查询所有表
            List<String> tables = jdbcTemplate.queryForList(
                "SHOW TABLES", String.class);
            
            result.put("status", "success");
            result.put("tables", tables);
            result.put("count", tables.size());
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "查询表失败: " + e.getMessage());
        }
        
        return result;
    }

    @GetMapping("/patient-count")
    public Map<String, Object> getPatientCount() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM patient_user", Integer.class);
            
            result.put("status", "success");
            result.put("table", "patient_user");
            result.put("count", count);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "查询患者数量失败: " + e.getMessage());
        }
        
        return result;
    }

    @GetMapping("/doctor-count")
    public Map<String, Object> getDoctorCount() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM doctor_user", Integer.class);
            
            result.put("status", "success");
            result.put("table", "doctor_user");
            result.put("count", count);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "查询医生数量失败: " + e.getMessage());
        }
        
        return result;
    }

    @GetMapping("/question-count")
    public Map<String, Object> getQuestionCount() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM question", Integer.class);
            
            result.put("status", "success");
            result.put("table", "question");
            result.put("count", count);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "查询问题数量失败: " + e.getMessage());
        }
        
        return result;
    }
}
