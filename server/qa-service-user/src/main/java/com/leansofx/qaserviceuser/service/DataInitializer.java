package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.entity.Department;
import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.entity.Specialty;
import com.leansofx.qaserviceuser.repository.DepartmentRepository;
import com.leansofx.qaserviceuser.repository.DoctorRepository;
import com.leansofx.qaserviceuser.repository.SpecialtyRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataInitializer {
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private SpecialtyRepository specialtyRepository;
    
    @PostConstruct
    @Transactional
    public void init() {
        // 检查是否已经有数据
        if (doctorRepository.count() > 0) {
            System.out.println("Database already initialized with " + doctorRepository.count() + " doctors.");
            return;
        }
        
        System.out.println("Initializing database with sample data...");
        
        // 创建科室
        Map<String, Department> departments = createDepartments();
        
        // 创建专业领域
        Map<String, Specialty> specialties = createSpecialties();
        
        // 创建医生数据
        createDoctors(departments, specialties);
        
        System.out.println("Database initialization completed. Total doctors: " + doctorRepository.count());
    }
    
    private Map<String, Department> createDepartments() {
        Map<String, Department> departmentMap = new HashMap<>();
        
        List<Department> departments = Arrays.asList(
            new Department("心内科", "cardiology"),
            new Department("儿科", "pediatrics"),
            new Department("骨科", "orthopedics"),
            new Department("妇产科", "obstetrics_gynecology"),
            new Department("消化内科", "gastroenterology")
        );
        
        for (Department dept : departments) {
            dept.setDescription(getDepartmentDescription(dept.getName()));
            departmentRepository.save(dept);
            departmentMap.put(dept.getName(), dept);
        }
        
        return departmentMap;
    }
    
    private Map<String, Specialty> createSpecialties() {
        Map<String, Specialty> specialtyMap = new HashMap<>();
        
        // 专业领域映射
        Map<String, String> specialtyData = new HashMap<>();
        specialtyData.put("高血压", "hypertension");
        specialtyData.put("冠心病", "coronary_heart_disease");
        specialtyData.put("心律失常", "arrhythmia");
        specialtyData.put("儿童感冒", "pediatric_cold");
        specialtyData.put("儿童发育", "child_development");
        specialtyData.put("疫苗接种", "vaccination");
        specialtyData.put("骨折", "fracture");
        specialtyData.put("关节炎", "arthritis");
        specialtyData.put("运动损伤", "sports_injury");
        specialtyData.put("孕期保健", "prenatal_care");
        specialtyData.put("妇科炎症", "gynecological_inflammation");
        specialtyData.put("产后恢复", "postpartum_recovery");
        specialtyData.put("胃炎", "gastritis");
        specialtyData.put("肠道疾病", "intestinal_disease");
        specialtyData.put("肝病", "liver_disease");
        
        for (Map.Entry<String, String> entry : specialtyData.entrySet()) {
            Specialty specialty = new Specialty(entry.getKey(), entry.getValue());
            specialty.setDescription(getSpecialtyDescription(entry.getKey()));
            specialtyRepository.save(specialty);
            specialtyMap.put(entry.getKey(), specialty);
        }
        
        return specialtyMap;
    }
    
    private void createDoctors(Map<String, Department> departments, Map<String, Specialty> specialties) {
        // 医生数据
        List<Map<String, Object>> doctorsData = Arrays.asList(
            createDoctorData("doc001", "dr-zhang-wei", "张伟医生", "主任医师", "心内科", 
                "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400",
                "15年临床经验", true, Arrays.asList("高血压", "冠心病", "心律失常")),
            createDoctorData("doc002", "dr-li-na", "李娜医生", "副主任医师", "儿科",
                "https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg?auto=compress&cs=tinysrgb&w=400",
                "10年临床经验", true, Arrays.asList("儿童感冒", "儿童发育", "疫苗接种")),
            createDoctorData("doc003", "dr-wang-qiang", "王强医生", "主治医师", "骨科",
                "https://images.pexels.com/photos/5452293/pexels-photo-5452293.jpeg?auto=compress&cs=tinysrgb&w=400",
                "8年临床经验", true, Arrays.asList("骨折", "关节炎", "运动损伤")),
            createDoctorData("doc004", "dr-liu-min", "刘敏医生", "主任医师", "妇产科",
                "https://images.pexels.com/photos/5452201/pexels-photo-5452201.jpeg?auto=compress&cs=tinysrgb&w=400",
                "18年临床经验", false, Arrays.asList("孕期保健", "妇科炎症", "产后恢复")),
            createDoctorData("doc005", "dr-chen-jie", "陈杰医生", "副主任医师", "消化内科",
                "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400",
                "12年临床经验", true, Arrays.asList("胃炎", "肠道疾病", "肝病"))
        );
        
        for (Map<String, Object> doctorData : doctorsData) {
            Doctor doctor = new Doctor();
            doctor.setDoctorId((String) doctorData.get("id"));
            doctor.setUsername((String) doctorData.get("username"));
            doctor.setPassword("$2a$10$YourEncryptedPasswordHere"); // 简化密码
            doctor.setName((String) doctorData.get("name"));
            doctor.setTitle((String) doctorData.get("title"));
            doctor.setDepartment(departments.get(doctorData.get("department")));
            doctor.setAvatar((String) doctorData.get("avatar"));
            doctor.setExperience((String) doctorData.get("experience"));
            doctor.setIsActive((Boolean) doctorData.get("isActive"));
            
            // 设置专业领域
            List<String> specialtyNames = (List<String>) doctorData.get("specialties");
            Set<Specialty> doctorSpecialties = specialtyNames.stream()
                .map(specialties::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            doctor.setSpecialties(doctorSpecialties);
            
            doctorRepository.save(doctor);
        }
    }
    
    private Map<String, Object> createDoctorData(String id, String username, String name, String title, 
                                                 String department, String avatar, String experience, 
                                                 boolean isActive, List<String> specialties) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("username", username);
        data.put("name", name);
        data.put("title", title);
        data.put("department", department);
        data.put("avatar", avatar);
        data.put("experience", experience);
        data.put("isActive", isActive);
        data.put("specialties", specialties);
        return data;
    }
    
    private String getDepartmentDescription(String name) {
        Map<String, String> descriptions = new HashMap<>();
        descriptions.put("心内科", "心血管内科，专注于心脏和血管疾病的诊断与治疗");
        descriptions.put("儿科", "儿童医疗服务，涵盖新生儿到青少年阶段的健康问题");
        descriptions.put("骨科", "骨骼、关节、肌肉和韧带相关疾病的治疗");
        descriptions.put("妇产科", "女性生殖系统疾病和孕产期保健");
        descriptions.put("消化内科", "消化系统疾病的诊断与治疗");
        return descriptions.getOrDefault(name, name + "科室");
    }
    
    private String getSpecialtyDescription(String name) {
        return name + "专业领域，提供相关疾病的诊断与治疗服务";
    }
}