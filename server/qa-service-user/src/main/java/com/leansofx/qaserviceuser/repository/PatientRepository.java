package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
    
    /**
     * 根据用户名查找患者
     * @param username 用户名
     * @return 患者信息
     */
    Optional<Patient> findByUsername(String username);
    
    /**
     * 根据用户名和密码查找患者
     * @param username 用户名
     * @param password 密码
     * @return 患者信息
     */
    Optional<Patient> findByUsernameAndPassword(String username, String password);
    
    /**
     * 根据手机号查找患者
     * @param phone 手机号
     * @return 患者信息
     */
    Optional<Patient> findByPhone(String phone);
    
    /**
     * 根据姓名和生日查找患者
     * @param name 姓名
     * @param birthday 生日
     * @return 患者信息
     */
    Optional<Patient> findByNameAndBirthday(String name, String birthday);
    
    /**
     * 查找所有活跃的患者
     * @return 活跃患者列表
     */
    List<Patient> findByIsActiveTrue();
    
    /**
     * 根据用户名和活动状态查找患者
     * @param username 用户名
     * @param isActive 是否活跃
     * @return 患者信息
     */
    Optional<Patient> findByUsernameAndIsActive(String username, Boolean isActive);
    
    /**
     * 判断用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 判断手机号是否存在
     * @param phone 手机号
     * @return 是否存在
     */
    boolean existsByPhone(String phone);
}