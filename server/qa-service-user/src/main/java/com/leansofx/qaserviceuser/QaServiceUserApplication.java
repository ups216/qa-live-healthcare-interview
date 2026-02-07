package com.leansofx.qaserviceuser;

import com.leansofx.qaserviceuser.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class QaServiceUserApplication implements CommandLineRunner {

    @Autowired
    private DoctorService doctorService;

	public static void main(String[] args) {
		SpringApplication.run(QaServiceUserApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
        // 初始化医生数据
        doctorService.initDoctorData();
    }

}
