package com.leansofx.qaserviceuser.config;

import com.leansofx.qaserviceuser.model.Doctor;
import com.leansofx.qaserviceuser.service.DoctorService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private DoctorService doctorService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 检查数据库中是否已有医生数据
        try {
            if (doctorService.getAllDoctors().isEmpty()) {
                // 从 JSON 文件中读取医生数据
                ObjectMapper objectMapper = new ObjectMapper();
                try (InputStream inputStream = getClass().getResourceAsStream("/doctor-user-list.json")) {
                    if (inputStream != null) {
                        JsonNode rootNode = objectMapper.readTree(inputStream);
                        List<Doctor> doctors = new ArrayList<>();
                        for (JsonNode node : rootNode) {
                            Doctor doctor = new Doctor();
                            doctor.setId(node.get("id").asText());
                            doctor.setUsername(node.get("username").asText());
                            doctor.setPassword(node.get("password").asText());
                            doctor.setName(node.get("name").asText());
                            doctor.setTitle(node.get("title").asText());
                            doctor.setDepartment(node.get("department").asText());
                            doctor.setAvatar(node.get("avatar").asText());
                            doctor.setExperience(node.get("experience").asText());
                            // 处理 specialties 数组
                            JsonNode specialtiesNode = node.get("specialties");
                            if (specialtiesNode.isArray()) {
                                StringBuilder specialtiesBuilder = new StringBuilder();
                                for (JsonNode specialtyNode : specialtiesNode) {
                                    specialtiesBuilder.append(specialtyNode.asText()).append(",");
                                }
                                if (specialtiesBuilder.length() > 0) {
                                    specialtiesBuilder.deleteCharAt(specialtiesBuilder.length() - 1);
                                }
                                doctor.setSpecialties(specialtiesBuilder.toString());
                            }
                            doctor.setActive(node.get("isActive").asBoolean());
                            doctors.add(doctor);
                        }
                        // 保存医生数据到数据库
                        for (Doctor doctor : doctors) {
                            doctorService.saveDoctor(doctor);
                        }
                        System.out.println("医生数据初始化完成");
                    } else {
                        System.out.println("找不到 doctor-user-list.json 文件");
                    }
                }
            } else {
                System.out.println("数据库中已有医生数据，跳过初始化");
            }
        } catch (Exception e) {
            System.out.println("初始化数据时出错: " + e.getMessage());
        }
    }
}
