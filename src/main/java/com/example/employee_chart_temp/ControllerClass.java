package com.example.employee_chart_temp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/")
public class ControllerClass {
    @Autowired
    EmployeeRepo employeeRepo;
    @Autowired
    DesignationRepo designationRepo;



    //method to show all employees i.e GET(all)
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity showAllEmployee(){
        List<EmployeeInformation> employees = employeeRepo.findAll();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }


    //method to show all the required information of a particular employee i.e. GET(id)
    @GetMapping("/employee/{aid}")
    public Map get(@PathVariable("aid") int aid){
        Map<String,Object> map=new LinkedHashMap<>();
        List<EmployeeInformation> colleagues=null;
        EmployeeInformation manager=null;

        EmployeeInformation employeeInformation=employeeRepo.findByEmployeeId(aid);
        map.put("Employee",employeeInformation);

        if(employeeInformation.getManagerId()!=null){
            manager=employeeRepo.findByEmployeeId(employeeInformation.getManagerId());
            map.put("Manager",manager);

            colleagues=employeeRepo.findAllByManagerIdAndEmployeeIdIsNot(employeeInformation.getManagerId(),employeeInformation.getEmployeeId());
            map.put("Colleagues",colleagues);
        }

        List<EmployeeInformation> reportingTo =employeeRepo.findAllByManagerIdAndEmployeeIdIsNot(employeeInformation.getEmployeeId(),employeeInformation.getEmployeeId());
        map.put("ReportingTo",reportingTo);

        return map;

    }

    //method to add an employee to the organization i.e. POST
    @RequestMapping(value = "/employee", method = RequestMethod.POST)
    public EmployeeInformation allEmployee(@RequestBody EmployeePost employeePost){

        EmployeeInformation emp =new EmployeeInformation();
        emp.designationId=designationRepo.findByDesignation(employeePost.getDesignationName());

        emp.setEmployeeName(employeePost.getEmpName());
        emp.setManagerId(employeePost.getManagerId());
        employeeRepo.save(emp);
        return emp;
    }


}
