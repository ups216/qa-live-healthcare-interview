package com.leansofx.qaserviceuser.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DoctorController {

    @GetMapping(value = "/api/doctors", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getDoctors() {
        return "[{\"id\":1,\"name\":\"Dr. Sarah Chen\",\"title\":\"Chief Physician\",\"department\":\"Cardiology\",\"hospital\":\"City General Hospital\",\"avatar\":\"/images/doctor1.jpg\",\"specialty\":\"Heart disease treatment\"}," +
               "{\"id\":2,\"name\":\"Dr. Michael Lee\",\"title\":\"Attending Physician\",\"department\":\"Neurology\",\"hospital\":\"City General Hospital\",\"avatar\":\"/images/doctor2.jpg\",\"specialty\":\"Stroke and headache\"}]";
    }
}