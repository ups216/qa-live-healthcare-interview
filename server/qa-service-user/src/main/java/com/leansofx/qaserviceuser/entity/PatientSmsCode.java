package com.leansofx.qaserviceuser.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_sms_codes")
public class PatientSmsCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "phone", length = 11, nullable = false)
    private String phone;

    @Column(name = "code", length = 6, nullable = false)
    private String code;

    @Column(name = "type", length = 20, nullable = false)
    private String type;

    @Column(name = "used")
    private Integer used = 0;

    @Column(name = "error_count")
    private Integer errorCount = 0;

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public PatientSmsCode() {
    }

    public PatientSmsCode(String phone, String code, String type, int expireMinutes) {
        this.phone = phone;
        this.code = code;
        this.type = type;
        this.expireAt = LocalDateTime.now().plusMinutes(expireMinutes);
        this.used = 0;
        this.errorCount = 0;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getUsed() {
        return used;
    }

    public void setUsed(Integer used) {
        this.used = used;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireAt);
    }

    public boolean isValid() {
        return used == 0 && errorCount < 3 && !isExpired();
    }

    public void incrementErrorCount() {
        this.errorCount++;
    }

    public void markAsUsed() {
        this.used = 1;
    }
}
