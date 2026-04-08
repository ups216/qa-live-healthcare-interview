package com.leansofx.qaserviceuser.repository;

import com.leansofx.qaserviceuser.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, String> {

    // 根据姓名模糊查询
    List<Doctor> findByNameContaining(String name);

    // 根据科室查询
    List<Doctor> findByDepartment(String department);

    // 根据职称查询
    List<Doctor> findByTitle(String title);

    // 根据在线状态查询
    List<Doctor> findByIsActive(boolean isActive);

    // 多条件查询
    @Query("SELECT d FROM Doctor d WHERE " +
           "(:name IS NULL OR d.name LIKE %:name%) AND " +
           "(:department IS NULL OR d.department = :department) AND " +
           "(:title IS NULL OR d.title = :title) AND " +
           "(:isActive IS NULL OR d.isActive = :isActive)")
    List<Doctor> findByConditions(
            @Param("name") String name,
            @Param("department") String department,
            @Param("title") String title,
            @Param("isActive") Boolean isActive
    );

    // 获取所有科室列表
    @Query("SELECT DISTINCT d.department FROM Doctor d ORDER BY d.department")
    List<String> findAllDepartments();

    // 获取所有职称列表
    @Query("SELECT DISTINCT d.title FROM Doctor d ORDER BY d.title")
    List<String> findAllTitles();
}
