package com.leansofx.qaserviceuser.config;

import com.leansofx.qaserviceuser.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // CORS 预检放行，避免影响跨域
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            writeUnauthorized(response, "Missing or invalid Authorization header");
            return false;
        }

        String token = auth.substring(7).trim();
        // 只校验 JWT 是否过期（解析时一并验证签名，防止伪造）
        if (jwtUtil.isExpired(token)) {
            writeUnauthorized(response, "Token expired or invalid");
            return false;
        }

        try {
            Claims claims = jwtUtil.parseToken(token);
            request.setAttribute("username", claims.getSubject());
            request.setAttribute("role", claims.get("role"));
        } catch (Exception e) {
            writeUnauthorized(response, "Token expired or invalid");
            return false;
        }

        return true;
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
