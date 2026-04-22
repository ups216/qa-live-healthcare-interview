package com.leansofx.qaserviceuser.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "doctor_specialty")
public class DoctorSpecialty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_id", length = 20, nullable = false)
    private String doctorId;

    @Column(length = 100, nullable = false)
    private String specialty;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
}
