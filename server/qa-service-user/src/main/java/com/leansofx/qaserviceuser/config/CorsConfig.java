package com.leansofx.qaserviceuser.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * 跨域配置。
 *
 * <p><b>历史问题修复</b>：原实现存在双配置冲突 ——
 * {@code application.properties} 写 {@code allow-credentials=false}，
 * 而本类的 Bean 写 {@code allowCredentials(true)}，且优先级高于配置文件，
 * 导致实际行为与配置文件声明不一致、难以排查。
 *
 * <p>现已删除 properties 中的 {@code spring.web.cors.*} 配置，
 * 统一由本类收敛，并把允许来源从 {@code *} 收窄到前端开发服务器。
 */
@Configuration
public class CorsConfig {

    /** 允许的前端来源：Vite 开发服务器 + phpMyAdmin 同机调试场景 */
    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "http://localhost:5173",
            "http://127.0.0.1:5173"
    );

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 收窄来源：不再使用 "*"，避免任意站点携带凭证调用本服务
        configuration.setAllowedOrigins(ALLOWED_ORIGINS);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        // 需求3 前端通过 Authorization 头传递 JWT，属于非简单请求头，
        // 配合 setAllowedHeaders("*") 放行
        configuration.addExposedHeader("Authorization");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
