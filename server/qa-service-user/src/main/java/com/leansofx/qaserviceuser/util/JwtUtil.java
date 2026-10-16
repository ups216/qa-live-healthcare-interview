package com.leansofx.qaserviceuser.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类（JJWT 0.12.7）。
 *
 * <p><b>版本陷阱</b>：0.12.x 与 0.11.x 的 API 完全不同。
 * 0.11.x 常用的 {@code Jwts.parserBuilder().setSigningKey(key).build()} 在 0.12.x 已移除，
 * 必须改用 {@code Jwts.parser().verifyWith(key).build()} 与 {@code parseSignedClaims()}。
 */
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs) {
        // HS256 要求密钥 >= 256bit(32字节)，长度不足时 Keys.hmacShaKeyFor 会直接抛 WeakKeyException
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * 签发 token。
     *
     * @param subject  主题，本项目为患者 id
     * @param claims   附加信息，本项目携带 username 便于日志追踪
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 解析 token 并返回 Claims。
     *
     * @throws JwtException 签名不匹配、已过期或格式非法
     */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** 取 token 主题（患者 id），解析失败返回 null */
    public String parseSubjectSafely(String token) {
        try {
            return parseClaims(token).getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public long getExpirationMs() {
        return expirationMs;
    }
}
