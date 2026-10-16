package com.leansofx.qaserviceuser;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.leansofx.qaserviceuser.mapper")
public class QaServiceUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(QaServiceUserApplication.class, args);
	}

}
