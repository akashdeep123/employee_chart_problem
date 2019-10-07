package com.example.employee_chart_project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class EmployeeController {

    @Autowired
    EmployeeRepo employeeRepo;

    @RequestMapping("/")
    public String home(){
        return "index.html";
    }

}
