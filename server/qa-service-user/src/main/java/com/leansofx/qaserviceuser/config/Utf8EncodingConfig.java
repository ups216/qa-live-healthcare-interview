package com.leansofx.qaserviceuser.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.IOException;

/**
 * 强制 HTTP 响应使用 UTF-8，避免医生列表等接口中文乱码（双重编码）。
 */
@Configuration
public class Utf8EncodingConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public Filter utf8ResponseFilter() {
        return new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                if (response instanceof HttpServletResponse) {
                    HttpServletResponse res = (HttpServletResponse) response;
                    res.setCharacterEncoding("UTF-8");
                }
                chain.doFilter(request, response);
            }
        };
    }
}
