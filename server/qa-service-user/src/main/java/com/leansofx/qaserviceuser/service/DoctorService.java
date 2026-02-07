package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dto.DoctorDTO;
import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    // 获取所有医生
    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(DoctorDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 获取在线医生
    public List<DoctorDTO> getActiveDoctors() {
        return doctorRepository.findByIsActiveTrue().stream()
                .map(DoctorDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 根据ID获取医生
    public DoctorDTO getDoctorById(String id) {
        return doctorRepository.findById(id)
                .map(DoctorDTO::fromEntity)
                .orElse(null);
    }

    // 根据用户名获取医生
    public DoctorDTO getDoctorByUsername(String username) {
        Doctor doctor = doctorRepository.findByUsername(username);
        return doctor != null ? DoctorDTO.fromEntity(doctor) : null;
    }

    // 创建医生
    public DoctorDTO createDoctor(DoctorDTO doctorDTO) {
        Doctor doctor = DoctorDTO.toEntity(doctorDTO);
        Doctor savedDoctor = doctorRepository.save(doctor);
        return DoctorDTO.fromEntity(savedDoctor);
    }

    // 更新医生
    public DoctorDTO updateDoctor(String id, DoctorDTO doctorDTO) {
        if (!doctorRepository.existsById(id)) {
            return null;
        }
        Doctor doctor = DoctorDTO.toEntity(doctorDTO);
        doctor.setId(id);
        Doctor updatedDoctor = doctorRepository.save(doctor);
        return DoctorDTO.fromEntity(updatedDoctor);
    }

    // 删除医生
    public boolean deleteDoctor(String id) {
        if (!doctorRepository.existsById(id)) {
            return false;
        }
        doctorRepository.deleteById(id);
        return true;
    }

    // 初始化医生数据
    public void initDoctorData() {
        // 检查是否已有医生数据
        if (doctorRepository.count() > 0) {
            return;
        }

        // 创建默认医生数据
        List<Doctor> doctors = List.of(
                createDoctor("doc001", "dr-zhang-wei", "123456", "张伟医生", "主任医师", "心内科", 
                        "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400", 
                        "15年临床经验", List.of("高血压", "冠心病", "心律失常"), true),
                createDoctor("doc002", "dr-li-na", "123456", "李娜医生", "副主任医师", "儿科", 
                        "https://images.pexels.com/photos/5327585/pexels-photo-5327585.jpeg?auto=compress&cs=tinysrgb&w=400", 
                        "10年临床经验", List.of("儿童感冒", "儿童发育", "疫苗接种"), true),
                createDoctor("doc003", "dr-wang-qiang", "123456", "王强医生", "主治医师", "骨科", 
                        "https://images.pexels.com/photos/5452293/pexels-photo-5452293.jpeg?auto=compress&cs=tinysrgb&w=400", 
                        "8年临床经验", List.of("骨折", "关节炎", "运动损伤"), true),
                createDoctor("doc004", "dr-liu-min", "123456", "刘敏医生", "主任医师", "妇产科", 
                        "https://images.pexels.com/photos/5452201/pexels-photo-5452201.jpeg?auto=compress&cs=tinysrgb&w=400", 
                        "18年临床经验", List.of("孕期保健", "妇科炎症", "产后恢复"), false),
                createDoctor("doc005", "dr-chen-jie", "123456", "陈杰医生", "副主任医师", "消化内科", 
                        "https://images.pexels.com/photos/5215024/pexels-photo-5215024.jpeg?auto=compress&cs=tinysrgb&w=400", 
                        "12年临床经验", List.of("胃炎", "肠道疾病", "肝病"), true)
        );

        doctorRepository.saveAll(doctors);
    }

    // 辅助方法，创建医生实体
    private Doctor createDoctor(String id, String username, String password, String name, 
                               String title, String department, String avatar, 
                               String experience, List<String> specialties, boolean isActive) {
        Doctor doctor = new Doctor();
        doctor.setId(id);
        doctor.setUsername(username);
        doctor.setPassword(password);
        doctor.setName(name);
        doctor.setTitle(title);
        doctor.setDepartment(department);
        doctor.setAvatar(avatar);
        doctor.setExperience(experience);
        doctor.setSpecialtiesList(specialties);
        doctor.setActive(isActive);
        return doctor;
    }
}
