package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dto.DoctorVO;
import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.mapper.DoctorMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 医生业务逻辑层。
 *
 * <p>职责边界：只为 Controller 提供 {@link DoctorVO}，
 * 含密码的 {@link Doctor} 实体不越过本层。
 */
@Service
public class DoctorService {

    private final DoctorMapper doctorMapper;

    public DoctorService(DoctorMapper doctorMapper) {
        this.doctorMapper = doctorMapper;
    }

    /** 全部医生列表 */
    public List<DoctorVO> findAll() {
        List<Doctor> doctors = doctorMapper.selectAll();
        if (doctors == null) {
            return Collections.emptyList();
        }
        return doctors.stream().map(this::toVO).collect(Collectors.toList());
    }

    /** 仅在线医生 */
    public List<DoctorVO> findActive() {
        List<Doctor> doctors = doctorMapper.selectActive();
        if (doctors == null) {
            return Collections.emptyList();
        }
        return doctors.stream().map(this::toVO).collect(Collectors.toList());
    }

    /** 按主键查询，不存在返回 null（由 Controller 决定 404 处理） */
    public DoctorVO findById(String id) {
        Doctor doctor = doctorMapper.selectById(id);
        return doctor == null ? null : toVO(doctor);
    }

    /** 按登录名查询，不存在返回 null */
    public DoctorVO findByUsername(String username) {
        Doctor doctor = doctorMapper.selectByUsername(username);
        return doctor == null ? null : toVO(doctor);
    }

    private DoctorVO toVO(Doctor doctor) {
        DoctorVO vo = new DoctorVO();
        vo.setId(doctor.getId());
        vo.setUsername(doctor.getUsername());
        vo.setName(doctor.getName());
        vo.setTitle(doctor.getTitle());
        vo.setDepartment(doctor.getDepartment());
        vo.setAvatar(doctor.getAvatar());
        vo.setExperience(doctor.getExperience());
        // 保证对外始终是数组而非 null：前端 v-for 依赖其为 string[]
        vo.setSpecialties(doctor.getSpecialties() == null
                ? new ArrayList<>()
                : doctor.getSpecialties());
        // 保证对外始终是布尔：前端 :disabled / v-if 依赖真假判断
        vo.setIsActive(Boolean.TRUE.equals(doctor.getIsActive()));
        return vo;
    }
}
