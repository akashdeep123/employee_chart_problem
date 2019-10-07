package com.example.employee_chart_temp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@RequestMapping("/api")
@SpringBootApplication

public class EmployeeChartTempApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeChartTempApplication.class, args);
    }

}
