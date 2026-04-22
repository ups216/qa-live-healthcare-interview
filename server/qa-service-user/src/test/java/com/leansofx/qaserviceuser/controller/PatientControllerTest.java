package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.bean.Patient;
import com.leansofx.qaserviceuser.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PatientControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    private Patient testPatient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();

        // 创建测试患者
        testPatient = new Patient();
        testPatient.setId("1");
        testPatient.setName("张三");
        testPatient.setBirthday(LocalDate.of(1990, 1, 1));
        testPatient.setPhone("13800138000");
        testPatient.setGender("男");
        testPatient.setPassword("encodedPassword");
    }

    @Test
    void testLogin_Success() throws Exception {
        // 模拟登录成功
        when(patientService.login("张三", "password123")).thenReturn(testPatient);

        // 构建请求体
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("name", "张三");
        loginRequest.put("password", "password123");

        // 执行请求
        mockMvc.perform(post("/api/patients/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"张三\", \"password\": \"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.name").value("张三"));

        // 验证方法调用
        verify(patientService, times(1)).login("张三", "password123");
    }

    @Test
    void testLogin_Failure() throws Exception {
        // 模拟登录失败
        when(patientService.login("张三", "wrongPassword")).thenReturn(null);

        // 执行请求
        mockMvc.perform(post("/api/patients/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"张三\", \"password\": \"wrongPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));

        // 验证方法调用
        verify(patientService, times(1)).login("张三", "wrongPassword");
    }

    @Test
    void testLogin_MissingParams() throws Exception {
        // 执行请求（缺少密码）
        mockMvc.perform(post("/api/patients/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"张三\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("用户名和密码不能为空"));

        // 执行请求（缺少姓名）
        mockMvc.perform(post("/api/patients/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\": \"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("用户名和密码不能为空"));
    }

    @Test
    void testResetPassword_Success() throws Exception {
        // 模拟密码重置成功
        when(patientService.resetPassword("张三", LocalDate.of(1990, 1, 1), "newPassword123")).thenReturn(true);

        // 执行请求
        mockMvc.perform(post("/api/patients/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"张三\", \"birthday\": \"1990-01-01\", \"newPassword\": \"newPassword123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("密码重置成功"));

        // 验证方法调用
        verify(patientService, times(1)).resetPassword("张三", LocalDate.of(1990, 1, 1), "newPassword123");
    }

    @Test
    void testResetPassword_Failure() throws Exception {
        // 模拟密码重置失败
        when(patientService.resetPassword("张三", LocalDate.of(1990, 1, 1), "newPassword123")).thenReturn(false);

        // 执行请求
        mockMvc.perform(post("/api/patients/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"张三\", \"birthday\": \"1990-01-01\", \"newPassword\": \"newPassword123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("患者信息不存在"));

        // 验证方法调用
        verify(patientService, times(1)).resetPassword("张三", LocalDate.of(1990, 1, 1), "newPassword123");
    }

    @Test
    void testResetPassword_InvalidDate() throws Exception {
        // 执行请求（日期格式错误）
        mockMvc.perform(post("/api/patients/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"张三\", \"birthday\": \"1990/01/01\", \"newPassword\": \"newPassword123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("生日格式错误，应为YYYY-MM-DD"));
    }

    @Test
    void testChangePassword_Success() throws Exception {
        // 模拟密码修改成功
        when(patientService.changePassword("1", "oldPassword", "newPassword123")).thenReturn(true);

        // 执行请求
        mockMvc.perform(post("/api/patients/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": \"1\", \"oldPassword\": \"oldPassword\", \"newPassword\": \"newPassword123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("密码修改成功"));

        // 验证方法调用
        verify(patientService, times(1)).changePassword("1", "oldPassword", "newPassword123");
    }

    @Test
    void testChangePassword_Failure() throws Exception {
        // 模拟密码修改失败
        when(patientService.changePassword("1", "wrongPassword", "newPassword123")).thenReturn(false);

        // 执行请求
        mockMvc.perform(post("/api/patients/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": \"1\", \"oldPassword\": \"wrongPassword\", \"newPassword\": \"newPassword123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("旧密码错误或患者不存在"));

        // 验证方法调用
        verify(patientService, times(1)).changePassword("1", "wrongPassword", "newPassword123");
    }

    @Test
    void testVerifyPatient_Success() throws Exception {
        // 模拟患者验证成功
        when(patientService.getPatientByNameAndBirthday("张三", LocalDate.of(1990, 1, 1))).thenReturn(testPatient);

        // 执行请求
        mockMvc.perform(post("/api/patients/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"张三\", \"birthday\": \"1990-01-01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("患者验证成功"))
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.name").value("张三"));

        // 验证方法调用
        verify(patientService, times(1)).getPatientByNameAndBirthday("张三", LocalDate.of(1990, 1, 1));
    }

    @Test
    void testVerifyPatient_Failure() throws Exception {
        // 模拟患者验证失败
        when(patientService.getPatientByNameAndBirthday("张三", LocalDate.of(1990, 1, 1))).thenReturn(null);

        // 执行请求
        mockMvc.perform(post("/api/patients/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"张三\", \"birthday\": \"1990-01-01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("患者信息不存在"));

        // 验证方法调用
        verify(patientService, times(1)).getPatientByNameAndBirthday("张三", LocalDate.of(1990, 1, 1));
    }

    @Test
    void testVerifyPatient_InvalidDate() throws Exception {
        // 执行请求（日期格式错误）
        mockMvc.perform(post("/api/patients/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"张三\", \"birthday\": \"1990/01/01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("生日格式错误，应为YYYY-MM-DD"));
    }
}
