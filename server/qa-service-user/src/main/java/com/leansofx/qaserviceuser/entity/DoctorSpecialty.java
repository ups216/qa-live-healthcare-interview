package com.leansofx.qaserviceuser.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "doctor_specialties")
public class DoctorSpecialty implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_id", length = 36, nullable = false)
    private String doctorId;

    @Column(name = "specialty", length = 50, nullable = false)
    private String specialty;

    public DoctorSpecialty() {
    }

    public DoctorSpecialty(String doctorId, String specialty) {
        this.doctorId = doctorId;
        this.specialty = specialty;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}
