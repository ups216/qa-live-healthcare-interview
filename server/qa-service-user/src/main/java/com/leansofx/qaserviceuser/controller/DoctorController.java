package com.leansofx.qaserviceuser.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leansofx.qaserviceuser.dto.DoctorDto;
import com.leansofx.qaserviceuser.dto.ErrorResponse;
import com.leansofx.qaserviceuser.model.DoctorEntity;
import com.leansofx.qaserviceuser.repository.DoctorRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorRepository doctorRepository;
    private final ObjectMapper objectMapper;

    public DoctorController(DoctorRepository doctorRepository, ObjectMapper objectMapper) {
        this.doctorRepository = doctorRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public List<DoctorDto> listDoctors() {
        return doctorRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getByUsername(@PathVariable String username) {
        return doctorRepository.findByUsername(username)
                .<ResponseEntity<?>>map(d -> ResponseEntity.ok(toDto(d)))
                .orElseGet(() -> ResponseEntity.status(404).body(new ErrorResponse("NOT_FOUND")));
    }

    private DoctorDto toDto(DoctorEntity entity) {
        List<String> specialties = parseSpecialties(entity.getSpecialtiesJson());
        return new DoctorDto(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getName(),
                entity.getTitle(),
                entity.getDepartment(),
                entity.getAvatar(),
                entity.getExperience(),
                specialties,
                entity.isActive()
        );
    }

    private List<String> parseSpecialties(String specialtiesJson) {
        if (specialtiesJson == null || specialtiesJson.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(specialtiesJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}

