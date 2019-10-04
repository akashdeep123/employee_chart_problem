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

            EmployeeInformation empToReplace = employeeRepo.findByEmployeeId(empId);
            int managerId = empToReplace.getManagerId();
            employeeRepo.delete(empToReplace);//delete information of employee to be replaced.

            //change manager id for the employees that were working under employee to be replaced.
            List<EmployeeInformation> children=employeeRepo.findAllByManagerId(empId);
            for(EmployeeInformation emi : children){
                    emi.setManagerId(empId);
                    employeeRepo.save(emi);
            }

            //update remaining information of new employee
            emp.designationId = designationRepo.findByDesignation(employeePost.getDesignationName());
            emp.setEmployeeName(employeePost.getEmpName());
            emp.setManagerId(managerId);
            employeeRepo.save(emp);
        }
        //code to update the information of existing employee
        else{
            EmployeeInformation empToUpdate = employeeRepo.findByEmployeeId(empId);
            if(employeePost.getManagerId()!=null){
                //find the employee with given manager id
                EmployeeInformation temp=employeeRepo.findByEmployeeId(employeePost.getManagerId());
                //now find level of the employee with given manager id
                float levelOfNewManager = designationRepo.findByDesignation(temp.getDesignationId().getDesignation()).getLevel();

                //find level of current employee
                float currLevel =designationRepo.findByDesignation(empToUpdate.getDesignation()).getLevel();

                System.out.print("working : "+currLevel+"   "+levelOfNewManager);

                //if level of the employee is greater or equal to the level of manager
                if(currLevel > levelOfNewManager){
                    empToUpdate.setManagerId(empToUpdate.getManagerId());
                    employeeRepo.save(empToUpdate);
                   // return new ResponseEntity<>(empToUpdate,HttpStatus.OK);
                }
                else{
                    return new ResponseEntity<>("bad request",HttpStatus.BAD_REQUEST);
                }
            }
            if(employeePost.getDesignationName()!=null){
                //get current level of employee
                float currLevel = designationRepo.findByDesignation(empToUpdate.getDesignation()).getLevel();
                //find the level of new designation
                float newLevel = designationRepo.findByDesignation(employeePost.getDesignationName()).getLevel();
                if(newLevel > currLevel){
                    return new ResponseEntity<>("bad request",HttpStatus.BAD_REQUEST);
                }
                else{
                    //empToUpdate.setDesignationId();
                    employeeRepo.save(empToUpdate);
                }
            }
            if(employeePost.getEmpName()!=null){
                empToUpdate.setEmployeeName(employeePost.getEmpName());
                employeeRepo.save(empToUpdate);
            }

            //return updated information
            return new ResponseEntity<>(empToUpdate,HttpStatus.OK);
        }

        return new ResponseEntity<>(emp,HttpStatus.OK);
    }

    //method to add an employee to the organization
    @PostMapping("/employee")
    public ResponseEntity addEmployee(@RequestBody EmployeePost employeePost){
        EmployeeInformation empToAdd =new EmployeeInformation();
        if(employeePost.getEmpName()!=null && employeePost.getDesignationName()!=null && employeePost.getManagerId()!=null){
            //find employee to be manager
            EmployeeInformation temp = employeeRepo.findByEmployeeId(employeePost.getManagerId());
            //find level of manager
            float levelOfManager = designationRepo.findByDesignation(temp.designationId.getDesignation()).getLevel();
            //find level of employee
            float empLevel = designationRepo.findByDesignation(employeePost.getDesignationName()).getLevel();
            if(levelOfManager >= empLevel){
                return new ResponseEntity("bad request",HttpStatus.BAD_REQUEST);
            }
            else{
                empToAdd.setEmployeeName(employeePost.getEmpName());
                empToAdd.setManagerId(employeePost.getManagerId());
                empToAdd.setDesignationId(designationRepo.findByDesignation(employeePost.getDesignationName()));
                employeeRepo.save(empToAdd);
            }

        }
        else{
            return new ResponseEntity<>("Enter all the fields",HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(empToAdd,HttpStatus.OK);
    }

    //method to delete an employee form the organization
    @DeleteMapping("/employee/{empId}")
    public ResponseEntity deleteEmployee(@PathVariable("empId") int empId){

        EmployeeInformation empToDelete = employeeRepo.findByEmployeeId(empId);
        //if employee to delete is director
        if(empToDelete.getManagerId()==null){
            int count=0;
            for(EmployeeInformation temp : employeeRepo.findAllByManagerId(empId)){
                count++;
            }
            if(count==0){
                employeeRepo.delete(empToDelete);
                return new ResponseEntity<>(empToDelete,HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>("director has employes working under him",HttpStatus.BAD_REQUEST);
            }
        }
        //if employee to delete is not director
        else{
            List<EmployeeInformation> childEmployes = employeeRepo.findAllByManagerId(empId);
            for(EmployeeInformation updateChild : childEmployes){
                updateChild.setManagerId(empToDelete.getManagerId());
                employeeRepo.save(updateChild);
            }
            employeeRepo.delete(empToDelete);
            return new ResponseEntity<>(empToDelete,HttpStatus.OK);
        }

    }


}
