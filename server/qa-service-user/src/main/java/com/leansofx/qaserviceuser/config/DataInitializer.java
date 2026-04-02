package com.leansofx.qaserviceuser.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.service.DoctorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(DoctorService doctorService) {
        return args -> {
            if (doctorService.getAllDoctors().isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    // Note: In a real scenario, we might want to read from an external file or resource
                    // For now, I'll hardcode the data based on the JSON content provided
                    // Ideally we should read the file from the web module if accessible or copy it to resources
                    
                    // Since the file is in another module, let's just create the objects manually for simplicity and reliability
                    // or define a method to load from a known location.
                    
                    // Let's create the doctors manually to ensure data integrity without file path issues across modules
                    
                    Doctor d1 = new Doctor();
                    d1.setId("doc001");
                    d1.setUsername("dr-zhang-wei");
                    d1.setPassword("123456");
                    d1.setName("张伟医生");
                    d1.setTitle("主任医师");
                    d1.setDepartment("心内科");
                    d1.setAvatar("https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400");
                    d1.setExperience("15年临床经验");
                    d1.setSpecialties(List.of("高血压", "冠心病", "心律失常"));
                    d1.setIsActive(true);
                    
                    Doctor d2 = new Doctor();
                    d2.setId("doc002");
                    d2.setUsername("dr-li-na");
                    d2.setPassword("123456");
                    d2.setName("李娜医生");
                    d2.setTitle("副主任医师");
                    d2.setDepartment("儿科");
                    d2.setAvatar("https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg?auto=compress&cs=tinysrgb&w=400");
                    d2.setExperience("10年临床经验");
                    d2.setSpecialties(List.of("儿童感冒", "儿童发育", "疫苗接种"));
                    d2.setIsActive(true);
                    
                    Doctor d3 = new Doctor();
                    d3.setId("doc003");
                    d3.setUsername("dr-wang-qiang");
                    d3.setPassword("123456");
                    d3.setName("王强医生");
                    d3.setTitle("主治医师");
                    d3.setDepartment("骨科");
                    d3.setAvatar("https://images.pexels.com/photos/5452293/pexels-photo-5452293.jpeg?auto=compress&cs=tinysrgb&w=400");
                    d3.setExperience("8年临床经验");
                    d3.setSpecialties(List.of("骨折", "关节炎", "运动损伤"));
                    d3.setIsActive(true);
                    
                    Doctor d4 = new Doctor();
                    d4.setId("doc004");
                    d4.setUsername("dr-liu-min");
                    d4.setPassword("123456");
                    d4.setName("刘敏医生");
                    d4.setTitle("主任医师");
                    d4.setDepartment("妇产科");
                    d4.setAvatar("https://images.pexels.com/photos/5452201/pexels-photo-5452201.jpeg?auto=compress&cs=tinysrgb&w=400");
                    d4.setExperience("18年临床经验");
                    d4.setSpecialties(List.of("孕期保健", "妇科炎症", "产后恢复"));
                    d4.setIsActive(false);
                    
                    Doctor d5 = new Doctor();
                    d5.setId("doc005");
                    d5.setUsername("dr-chen-jie");
                    d5.setPassword("123456");
                    d5.setName("陈杰医生");
                    d5.setTitle("副主任医师");
                    d5.setDepartment("消化内科");
                    d5.setAvatar("https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400");
                    d5.setExperience("12年临床经验");
                    d5.setSpecialties(List.of("胃炎", "肠道疾病", "肝病"));
                    d5.setIsActive(true);
                    
                    doctorService.saveAll(List.of(d1, d2, d3, d4, d5));
                    System.out.println("Doctor data initialized successfully.");
                    
                } catch (Exception e) {
                    System.err.println("Failed to initialize doctor data: " + e.getMessage());
                }
            }
        };
    }
}
