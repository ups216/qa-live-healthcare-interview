package com.leansofx.qaserviceuser.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/doctors/**",   // 医生列表/活跃医生：公开，无需登录
                        "/api/auth/**",       // 登录接口本身：公开
                        "/api/test/**"        // 测试接口：公开
                );
        // 说明：现有接口（医生列表等）对应的页面不登录也能访问，已在白名单放行；
        // 后续需要登录的问诊/诊室接口（不在白名单的 /api 路径）将自动受 JWT 过期校验保护。
    }
}
