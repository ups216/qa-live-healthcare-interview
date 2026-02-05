package com.leansofx.qaserviceuser;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** 使用 test 配置（H2），无需 Docker MySQL */
@SpringBootTest
@ActiveProfiles("test")
class QaServiceUserApplicationTests {

	@Test
	void contextLoads() {
	}
}
