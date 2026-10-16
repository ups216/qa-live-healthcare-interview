package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.dto.LoginRequest;
import com.leansofx.qaserviceuser.dto.LoginResponse;
import com.leansofx.qaserviceuser.dto.PatientVO;
import com.leansofx.qaserviceuser.dto.RegisterRequest;
import com.leansofx.qaserviceuser.entity.Patient;
import com.leansofx.qaserviceuser.exception.BizException;
import com.leansofx.qaserviceuser.exception.ErrorCode;
import com.leansofx.qaserviceuser.mapper.PatientMapper;
import com.leansofx.qaserviceuser.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * 问诊用户业务逻辑（需求3：PATIENT-AUTH-001）。
 *
 * <p>职责边界：只向 Controller 返回 {@link PatientVO}，
 * 含密码的 {@link Patient} 实体不越过本层。
 */
@Service
public class PatientService {

    private static final Logger log = LoggerFactory.getLogger(PatientService.class);

    private static final String ID_PREFIX = "pt-";

    /** id 随机片段长度：pt-(3) + 12 = 15 字符，远小于 t_patient.id 的 VARCHAR(32) 上限 */
    private static final int ID_RANDOM_LENGTH = 12;

    private final PatientMapper patientMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public PatientService(PatientMapper patientMapper,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.patientMapper = patientMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 注册。
     *
     * <p>查重采用「应用层查询 + 数据库唯一索引」双保险：
     * 应用层查询给出友好报错，唯一索引兜住并发场景下的重复提交。
     */
    public PatientVO register(RegisterRequest request) {
        if (patientMapper.selectByUsername(request.getUsername()) != null) {
            throw new BizException(ErrorCode.PATIENT_USERNAME_EXISTS, "用户名已存在");
        }

        Patient patient = new Patient();
        patient.setId(generateId());
        patient.setUsername(request.getUsername());
        // 明文密码只在此处出现一次，随即被替换为 BCrypt 密文
        patient.setPassword(passwordEncoder.encode(request.getPassword()));
        patient.setName(request.getName());
        patient.setBirthday(request.getBirthday());
        patient.setPhone(request.getPhone());
        patient.setGender(request.getGender());

        try {
            patientMapper.insert(patient);
        } catch (DuplicateKeyException e) {
            // 并发场景下唯一索引生效，仍对外返回"用户名已存在"，避免暴露底层异常
            log.warn("并发注册触发唯一索引冲突，username={}", request.getUsername());
            throw new BizException(ErrorCode.PATIENT_USERNAME_EXISTS, "用户名已存在");
        }

        log.info("患者注册成功，username={}, id={}", request.getUsername(), patient.getId());
        return toVO(patient);
    }

    /**
     * 登录并签发 token。
     *
     * <p>安全设计：用户不存在与密码错误对外抛出<b>不同的内部 code</b>（1003 / 1004，便于日志排查），
     * 但 GlobalExceptionHandler 透出给客户端的 message 均为「用户名或密码错误」，
     * 以此防止攻击者通过响应差异枚举出已注册用户名。
     */
    public LoginResponse login(LoginRequest request) {
        Patient patient = patientMapper.selectByUsername(request.getUsername());
        if (patient == null) {
            throw new BizException(ErrorCode.PATIENT_NOT_FOUND, "用户名或密码错误");
        }

        if (!passwordEncoder.matches(request.getPassword(), patient.getPassword())) {
            throw new BizException(ErrorCode.PATIENT_BAD_CREDENTIALS, "用户名或密码错误");
        }

        String token = jwtUtil.generateToken(patient.getId(), Map.of("username", patient.getUsername()));
        log.info("患者登录成功，username={}", request.getUsername());

        return new LoginResponse(token, jwtUtil.getExpirationMs(), toVO(patient));
    }

    /** 按主键查询当前登录用户，未找到返回 null */
    public PatientVO findById(String id) {
        Patient patient = patientMapper.selectById(id);
        return patient == null ? null : toVO(patient);
    }

    private String generateId() {
        // 完整 UUID 去掉连字符是 32 位，再加上前缀会超出 VARCHAR(32)，故截取前 12 位
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, ID_RANDOM_LENGTH);
        return ID_PREFIX + random;
    }

    private PatientVO toVO(Patient patient) {
        PatientVO vo = new PatientVO();
        vo.setId(patient.getId());
        vo.setUsername(patient.getUsername());
        vo.setName(patient.getName());
        vo.setBirthday(patient.getBirthday());
        vo.setPhone(patient.getPhone());
        vo.setGender(patient.getGender());
        return vo;
    }
}
