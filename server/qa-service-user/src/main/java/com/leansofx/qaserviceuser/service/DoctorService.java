package com.leansofx.qaserviceuser.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leansofx.qaserviceuser.Entity.Doctor;
import com.leansofx.qaserviceuser.mapper.DoctorMapper;
import org.springframework.stereotype.Service;

@Service
public class DoctorService extends ServiceImpl<DoctorMapper, Doctor> {}
