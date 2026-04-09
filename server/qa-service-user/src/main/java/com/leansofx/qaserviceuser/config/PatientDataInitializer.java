package com.leansofx.qaserviceuser.config;

import com.leansofx.qaserviceuser.model.PatientEntity;
import com.leansofx.qaserviceuser.repository.PatientRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds demo patients when the table is empty (password for all: 123456).
 */
@Component
@Order(20)
public class PatientDataInitializer implements ApplicationRunner {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public PatientDataInitializer(PatientRepository patientRepository, PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (patientRepository.count() > 0) {
            return;
        }
        String hash = passwordEncoder.encode("123456");
        patientRepository.save(seed("patient001", "zhaoming", hash, "赵明", "1985-03-15", "138****1234", "男"));
        patientRepository.save(seed("patient002", "sunli", hash, "孙丽", "1990-07-22", "139****5678", "女"));
        patientRepository.save(seed("patient003", "zhoujie", hash, "周杰", "1978-11-08", "137****9012", "男"));
        patientRepository.save(seed("patient004", "wufang", hash, "吴芳", "1995-05-20", "136****3456", "女"));
        patientRepository.save(seed("patient005", "zhenghao", hash, "郑浩", "1988-09-12", "135****7890", "男"));
    }

    private static PatientEntity seed(
            String id,
            String username,
            String passwordHash,
            String name,
            String birthday,
            String phone,
            String gender
    ) {
        PatientEntity e = new PatientEntity();
        e.setId(id);
        e.setUsername(username);
        e.setPasswordHash(passwordHash);
        e.setName(name);
        e.setBirthday(birthday);
        e.setPhone(phone);
        e.setGender(gender);
        return e;
    }
}
