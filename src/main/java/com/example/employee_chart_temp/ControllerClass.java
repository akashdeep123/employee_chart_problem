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
        List<EmployeeInformation> employees = employeeRepo.findAllByOrderByDesignationId_levelAscEmployeeNameAsc();
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

    //method to replace an employee with new employee and to update the information of existing employee i.e. PUT
    @RequestMapping(value = "/employee/{empId}", method = RequestMethod.PUT)
    public ResponseEntity allEmployee(@PathVariable("empId") int empId, @RequestBody EmployeePost employeePost){

        EmployeeInformation emp = new EmployeeInformation();

        //code to replace
        if(employeePost.isReplace()){

            EmployeeInformation empToReplace = employeeRepo.findEmployeeInformationByEmployeeId(empId);
            int managerId = empToReplace.getManagerId();
            employeeRepo.delete(empToReplace);//delete information of employee to be replaced.

            //change manager id for the employees that were working under employee to be replaced.
            List<EmployeeInformation> children=employeeRepo.findAllByManagerId(empId);
            for(EmployeeInformation emi : children){
                    emi.setManagerId(empId);
                    employeeRepo.save(emi);
            }

            //update remainig information of new employee
            emp.designationId = designationRepo.findByDesignation(employeePost.getDesignationName());
            emp.setEmployeeName(employeePost.getEmpName());
            emp.setManagerId(managerId);
            employeeRepo.save(emp);
        }
        //code to update the information of existing employee
        else{
            EmployeeInformation empToUpdate = employeeRepo.findByEmployeeId(empId);
            if(empToUpdate.getManagerId()!=null){
                //find the employee with given manager id
                EmployeeInformation temp=employeeRepo.findByEmployeeId(empToUpdate.getManagerId());
                //now find level of the employee with given manager id
                float levelOfManager = designationRepo.findByDesignation(temp.getDesignationId().getDesignation()).getLevel();
                //find level of employee according to given information
                float newLevel = designationRepo.findByDesignation(empToUpdate.getDesignation()).getLevel();

                //if level of the employee is greater or equal to the level of manager
                if(newLevel >= levelOfManager){
                    return new ResponseEntity<>("bad request",HttpStatus.BAD_REQUEST);
                }
                else{
                    return new ResponseEntity<>("ok",HttpStatus.OK);
                }
            }
            if(empToUpdate.getEmployeeName()!=null){
                
            }
        }

        return new ResponseEntity<>(emp,HttpStatus.OK);
    }



    //method to delete an employee form the organization
    @DeleteMapping("/employee/{empId}")
    public void deleteEmployee(@PathVariable("empId") int empId){

        //EmployeeInformation employeeInformation=new EmployeeInformation();
        //employeeRepo.deleteByEmployeeId(empId);

    }


}
