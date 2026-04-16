package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    /**
     * 首页接口 - 获取激活状态的医生列表
     */
    @GetMapping("/home/list")
    public Map<String, Object> getHomeDoctorList() {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorService.getActiveDoctorList();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", doctors);
        return response;
    }

    /**
     * 医生页面接口 - 分页获取医生列表
     */
    @GetMapping("/list")
    public Map<String, Object> getDoctorList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorService.getDoctorListByPage(page, pageSize);
        int total = doctorService.getDoctorList().size();
        
        Map<String, Object> pageData = new HashMap<>();
        pageData.put("list", doctors);
        pageData.put("total", total);
        pageData.put("page", page);
        pageData.put("pageSize", pageSize);
        pageData.put("totalPages", (int) Math.ceil((double) total / pageSize));
        
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", pageData);
        return response;
    }

    /**
     * 获取医生详情
     */
    @GetMapping("/detail/{id}")
    public Map<String, Object> getDoctorDetail(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        Doctor doctor = doctorService.getDoctorById(id);
        if (doctor != null) {
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", doctor);
        } else {
            response.put("code", 404);
            response.put("message", "医生不存在");
            response.put("data", null);
        }
        return response;
    }

    /**
     * 根据科室获取医生列表
     */
    @GetMapping("/department/{department}")
    public Map<String, Object> getDoctorListByDepartment(@PathVariable String department) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorService.getDoctorListByDepartment(department);
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", doctors);
        return response;
    }
}
