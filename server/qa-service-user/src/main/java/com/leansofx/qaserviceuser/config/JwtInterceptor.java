package com.leansofx.qaserviceuser.config;

import com.leansofx.qaserviceuser.exception.BizException;
import com.leansofx.qaserviceuser.exception.ErrorCode;
import com.leansofx.qaserviceuser.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * JWT 鉴权拦截器（需求3）。
 *
 * <p>白名单之外的路径一律要求 {@code Authorization: Bearer <token>}。
 * 解析成功后把患者 id 写入 request attribute，供 Controller 直接取用，
 * 避免每个接口重复解析一遍 token。
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtInterceptor.class);

    /**
     * 无需鉴权的路径。
     *
     * <p>为什么 /api/doctors/** 要放行：医生列表是公开信息，
     * 首页与医生页在匿名状态下也要能浏览，否则整站首屏会白屏。
     * <p>为什么 /api/test/** 与 /actuator/** 要放行：它们是既有接口与健康探针，
     * 被拦截会直接破坏回归测试。
     */
    private static final List<String> WHITELIST = List.of(
            "/api/doctors/**",
            "/api/patient/login",
            "/api/patient/register",
            "/api/test/**",
            "/actuator/**"
    );

    /** 存放患者 id 的 request attribute 名 */
    public static final String ATTR_PATIENT_ID = "patientId";

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        String requestUri = request.getRequestURI();

        if (isWhitelisted(requestUri)) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            // 缺少 Authorization 头或缺少 Bearer 前缀
            throw new BizException(ErrorCode.TOKEN_MISSING, "未登录或登录已过期");
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            throw new BizException(ErrorCode.TOKEN_MISSING, "未登录或登录已过期");
        }

        String patientId;
        try {
            patientId = jwtUtil.parseClaims(token).getSubject();
        } catch (Exception e) {
            // 过期、签名不匹配、格式非法统一视为无效 token，
            // 不向客户端暴露具体原因，避免给攻击者提供反馈
            log.debug("token 解析失败: {}", e.getMessage());
            throw new BizException(ErrorCode.TOKEN_INVALID, "token 无效或已过期");
        }

        request.setAttribute(ATTR_PATIENT_ID, patientId);
        return true;
    }

    private boolean isWhitelisted(String requestUri) {
        return WHITELIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestUri));
    }
}
