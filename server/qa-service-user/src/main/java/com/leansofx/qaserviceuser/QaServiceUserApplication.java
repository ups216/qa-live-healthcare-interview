package com.leansofx.qaserviceuser;

import com.leansofx.qaserviceuser.entity.Doctor;
import com.leansofx.qaserviceuser.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class QaServiceUserApplication implements CommandLineRunner {

    @Autowired
    private DoctorService doctorService;

    public static void main(String[] args) {
        SpringApplication.run(QaServiceUserApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 添加测试数据
        List<Doctor> doctors = new ArrayList<>();
        
        doctors.add(new Doctor(
                "doc001",
                "zhangwei",
                "123456",
                "张伟",
                "主任医师",
                "心内科",
                "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20doctor%20portrait%20male&image_size=square",
                "20年临床经验",
                "[\"冠心病\",\"心力衰竭\",\"高血压\"]",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        ));
        
        doctors.add(new Doctor(
                "doc002",
                "lina",
                "123456",
                "李娜",
                "副主任医师",
                "神经内科",
                "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20doctor%20portrait%20female&image_size=square",
                "15年临床经验",
                "[\"脑血管病\",\"癫痫\",\"帕金森病\"]",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        ));
        
        doctors.add(new Doctor(
                "doc003",
                "wangqiang",
                "123456",
                "王强",
                "主治医师",
                "消化内科",
                "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20doctor%20portrait%20male%20asian&image_size=square",
                "10年临床经验",
                "[\"胃溃疡\",\"结肠炎\",\"肝炎\"]",
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        ));
        
        doctors.add(new Doctor(
                "doc004",
                "zhaomin",
                "123456",
                "赵敏",
                "主任医师",
                "呼吸内科",
                "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20doctor%20portrait%20female%20asian&image_size=square",
                "25年临床经验",
                "[\"哮喘\",\"肺炎\",\"慢性阻塞性肺疾病\"]",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        ));
        
        doctors.add(new Doctor(
                "doc005",
                "liuwei",
                "123456",
                "刘伟",
                "副主任医师",
                "骨科",
                "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20doctor%20portrait%20male%20middle%20age&image_size=square",
                "18年临床经验",
                "[\"骨折\",\"关节炎\",\"腰椎间盘突出\"]",
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        ));
        
        for (Doctor doctor : doctors) {
            doctorService.createDoctor(doctor);
        }
        
        System.out.println("测试数据添加完成！");
    }
}
