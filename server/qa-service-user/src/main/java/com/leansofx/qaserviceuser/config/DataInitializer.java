package com.leansofx.qaserviceuser.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.entity.PatientUser;
import com.leansofx.qaserviceuser.mapper.DoctorMapper;
import com.leansofx.qaserviceuser.mapper.PatientUserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(DoctorMapper doctorMapper, PatientUserMapper patientUserMapper, ObjectMapper objectMapper) {
        return args -> {
            seedDoctors(doctorMapper, objectMapper);
            seedPatients(patientUserMapper, objectMapper);
        };
    }

    private void seedDoctors(DoctorMapper doctorMapper, ObjectMapper objectMapper) throws IOException {
        if (doctorMapper.selectCount(null) > 0) {
            return;
        }

        List<Map<String, Object>> doctors = objectMapper.readValue(readProjectFile("doctor-user-list.json"), new TypeReference<>() {});
        for (Map<String, Object> item : doctors) {
            Doctor doctor = new Doctor();
            doctor.setId(stringValue(item.get("id")));
            doctor.setUsername(stringValue(item.get("username")));
            doctor.setPassword(stringValue(item.get("password")));
            doctor.setName(stringValue(item.get("name")));
            doctor.setTitle(stringValue(item.get("title")));
            doctor.setDepartment(stringValue(item.get("department")));
            doctor.setAvatar(stringValue(item.get("avatar")));
            doctor.setExperience(stringValue(item.get("experience")));
            doctor.setSpecialties(String.join(",", listValue(item.get("specialties"))));
            doctor.setActive(Boolean.parseBoolean(String.valueOf(item.get("isActive"))));
            doctorMapper.insert(doctor);
        }
    }

    private void seedPatients(PatientUserMapper patientUserMapper, ObjectMapper objectMapper) throws IOException {
        if (patientUserMapper.selectCount(null) > 0) {
            return;
        }

        List<Map<String, Object>> patients = objectMapper.readValue(readProjectFile("patient-user.json"), new TypeReference<>() {});
        for (Map<String, Object> item : patients) {
            PatientUser patient = new PatientUser();
            String id = stringValue(item.get("id"));
            patient.setId(id);
            patient.setUsername(id);
            patient.setPassword("123456");
            patient.setDisplayName(stringValue(item.get("name")));
            if (!stringValue(item.get("birthday")).isBlank()) {
                patient.setBirthday(LocalDate.parse(stringValue(item.get("birthday"))));
            }
            patient.setPhone(stringValue(item.get("phone")));
            patient.setGender(stringValue(item.get("gender")));
            patientUserMapper.insert(patient);
        }
    }

    private String readProjectFile(String fileName) throws IOException {
        Path dataFile = findDataFile(fileName);
        return Files.readString(dataFile);
    }

    private Path findDataFile(String fileName) throws IOException {
        Path current = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        while (current != null) {
            Path candidate = current.resolve("web").resolve("qa-web").resolve("src").resolve("data").resolve(fileName);
            if (Files.exists(candidate)) {
                return candidate;
            }
            current = current.getParent();
        }
        throw new IOException("Unable to locate seed data file: " + fileName);
    }

    @SuppressWarnings("unchecked")
    private List<String> listValue(Object value) {
        return value == null ? List.of() : (List<String>) value;
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
