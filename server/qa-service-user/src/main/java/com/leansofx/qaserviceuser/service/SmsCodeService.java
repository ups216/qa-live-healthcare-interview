package com.leansofx.qaserviceuser.service;

import com.leansofx.qaserviceuser.entity.PatientSmsCode;
import com.leansofx.qaserviceuser.repository.PatientSmsCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SmsCodeService {

    private static final int CODE_VALID_MINUTES = 5;
    private static final int SEND_INTERVAL_SECONDS = 60;
    private static final String MOCK_CODE = "123456";
    private static final String TYPE_REGISTER = "register";

    private final PatientSmsCodeRepository smsCodeRepository;

    public SmsCodeService(PatientSmsCodeRepository smsCodeRepository) {
        this.smsCodeRepository = smsCodeRepository;
    }

    @Transactional
    public SendResult sendCode(String phone, String type) {
        Optional<PatientSmsCode> latestCode = smsCodeRepository.findTopByPhoneAndTypeOrderByCreatedAtDesc(phone, type);

        if (latestCode.isPresent()) {
            PatientSmsCode code = latestCode.get();
            if (!code.isExpired()) {
                long secondsSinceCreated = java.time.Duration.between(code.getCreatedAt(), LocalDateTime.now()).getSeconds();
                if (secondsSinceCreated < SEND_INTERVAL_SECONDS) {
                    long remainingSeconds = SEND_INTERVAL_SECONDS - secondsSinceCreated;
                    return SendResult.error(42901, "发送过于频繁，请" + remainingSeconds + "秒后重试");
                }
            }
        }

        String code = generateCode();
        PatientSmsCode smsCode = new PatientSmsCode(phone, code, type, CODE_VALID_MINUTES);
        smsCodeRepository.save(smsCode);

        System.out.println("[SMS Mock] 发送验证码到 " + phone + ": " + code);

        return SendResult.success(CODE_VALID_MINUTES * 60);
    }

    @Transactional
    public VerifyResult verifyCode(String phone, String code, String type) {
        Optional<PatientSmsCode> smsCodeOpt = smsCodeRepository.findLatestValidByPhoneAndType(phone, type, LocalDateTime.now());

        if (smsCodeOpt.isEmpty()) {
            return VerifyResult.error(40004, "验证码已过期");
        }

        PatientSmsCode smsCode = smsCodeOpt.get();

        if (smsCode.getErrorCount() >= 3) {
            return VerifyResult.error(40005, "验证码已失效，请重新获取");
        }

        if (!smsCode.getCode().equals(code)) {
            smsCode.incrementErrorCount();
            smsCodeRepository.save(smsCode);
            return VerifyResult.error(40003, "验证码错误");
        }

        smsCode.markAsUsed();
        smsCodeRepository.save(smsCode);

        return VerifyResult.success();
    }

    private String generateCode() {
        return MOCK_CODE;
    }

    public static class SendResult {
        private final boolean success;
        private final int code;
        private final String message;
        private final int expiresIn;

        private SendResult(boolean success, int code, String message, int expiresIn) {
            this.success = success;
            this.code = code;
            this.message = message;
            this.expiresIn = expiresIn;
        }

        public static SendResult success(int expiresIn) {
            return new SendResult(true, 0, "验证码已发送", expiresIn);
        }

        public static SendResult error(int code, String message) {
            return new SendResult(false, code, message, 0);
        }

        public boolean isSuccess() { return success; }
        public int getCode() { return code; }
        public String getMessage() { return message; }
        public int getExpiresIn() { return expiresIn; }
    }

    public static class VerifyResult {
        private final boolean success;
        private final int code;
        private final String message;

        private VerifyResult(boolean success, int code, String message) {
            this.success = success;
            this.code = code;
            this.message = message;
        }

        public static VerifyResult success() {
            return new VerifyResult(true, 0, "验证成功");
        }

        public static VerifyResult error(int code, String message) {
            return new VerifyResult(false, code, message);
        }

        public boolean isSuccess() { return success; }
        public int getCode() { return code; }
        public String getMessage() { return message; }
    }
}
