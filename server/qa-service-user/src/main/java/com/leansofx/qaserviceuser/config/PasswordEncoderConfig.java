package com.leansofx.qaserviceuser.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密配置。
 *
 * <p>只引入 spring-security-crypto 模块，而非完整的 spring-boot-starter-security。
 * 理由：完整 Security 会带来默认登录页、默认鉴权与 CSRF，
 * 会把既有裸接口（/api/test/cors、/api/doctors/**）一并挡住，改造成本陡增。
 *
 * <p>BCrypt 自带随机盐，相同明文每次产出不同密文，输出固定 60 字符（$2a$10$...）。
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // cost factor 10：单次哈希约 100ms 量级，兼顾安全与登录响应速度
        return new BCryptPasswordEncoder();
    }
}
