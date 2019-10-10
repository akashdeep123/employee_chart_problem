package com.example.employee_chart_temp.Controller;

import com.example.employee_chart_temp.Repositories.DesignationRepo;
import com.example.employee_chart_temp.Repositories.EmployeeRepo;
import com.example.employee_chart_temp.Services.EmployeeServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/")
public class ControllerClass {

    @Autowired
    EmployeeRepo employeeRepo;
    @Autowired
    DesignationRepo designationRepo;
    @Autowired
    EmployeeServices employeeServices;

    //method to show all employees i.e GET(all)
    @RequestMapping(method = RequestMethod.GET,path = "/rest/employees")
    public ResponseEntity showAllEmployees(){
       return employeeServices.returnAllEmployee();
    }

    //method to show all the required information of a particular employee i.e. GET(id)
    @GetMapping("/rest/employees/{aid}")
    public ResponseEntity get(@PathVariable("aid") int aid){

        Map map = employeeServices.get(aid);
        if(map == null){
            return new ResponseEntity("Employee record not found",HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(map,HttpStatus.OK);

    }



    //method to add an employee to the organization
    @PostMapping("/rest/employees")
    public ResponseEntity addEmployee(@RequestBody EmployeePost employeePost){

        return employeeServices.addAnEmployee(employeePost);

    }

    //method to replace an employee with new employee and to update the information of existing employee i.e. PUT
    @RequestMapping(value = "/rest/employees/{empId}", method = RequestMethod.PUT)
    public ResponseEntity updateOrReplaceEmployee(@PathVariable("empId") int empId, @RequestBody EmployeePut employeePut){

        //code to replace
        if(employeePut.isReplace()){
            return employeeServices.replace(empId,employeePut);
        }
        //code to update the information of existing employee
        else{
            return employeeServices.update(empId,employeePut);
        }

    }

    //method to delete an employee form the organization
    @DeleteMapping("/rest/employees/{empId}")
    public ResponseEntity deleteEmployee(@PathVariable("empId") int empId){

        return employeeServices.deleteAnEmployee(empId);

    }

}
