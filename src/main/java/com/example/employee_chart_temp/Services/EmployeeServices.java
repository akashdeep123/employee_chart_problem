package com.example.employee_chart_temp.Services;

import com.example.employee_chart_temp.Controller.EmployeePost;
import com.example.employee_chart_temp.Entities.EmployeeInformation;
import com.example.employee_chart_temp.Repositories.DesignationRepo;
import com.example.employee_chart_temp.Repositories.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeServices {

    @Autowired
    EmployeeRepo employeeRepo;
    @Autowired
    DesignationRepo designationRepo;
    @Autowired
    Validations validations;

    public ResponseEntity returnAllEmployee(){
        List<EmployeeInformation> employees = employeeRepo.findAllByOrderByDesignationId_levelAscEmployeeNameAsc();
        if(employees.size()>0)
            return new ResponseEntity<>(employees, HttpStatus.OK);
        else
            return new ResponseEntity<>("No Record Found",HttpStatus.NOT_FOUND);
    }

    public Map get(int aid){
        //if user enters invalid employee id
        if(! validations.isEmpIdValid(aid)){
            return null;
        }
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

    public ResponseEntity replace(int empId, EmployeePost employeePost){
        if(! validations.isEmpIdValid(empId)){
            return new ResponseEntity<>("employee not found",HttpStatus.BAD_REQUEST);
        }else{
            //if employee to be replaced with is not new employee, i.e. replacement can;t be done with the existing employee
            if(validations.isEmpIdValid(employeePost.getEmpId())){
                return new ResponseEntity<>("can't replace with the existing employee",HttpStatus.BAD_REQUEST);
            }
        }
        if(!validations.isDesignationValid(employeePost.getDesignationName())){
            return new ResponseEntity<>("designation invalid",HttpStatus.BAD_REQUEST);
        }
        EmployeeInformation emp = new EmployeeInformation();
        EmployeeInformation empToReplace = employeeRepo.findByEmployeeId(empId);
        Integer managerId = empToReplace.getManagerId();

        if(validations.isParentChildRelation(employeePost,empId)/*designationRepo.findByDesignation(employeePost.getDesignationName()).getLevel() <= designationRepo.findByDesignation(empToReplace.designationId.getDesignation()).getLevel()*/){
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
        else{
            return new ResponseEntity<>("replacement not possible", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(emp,HttpStatus.OK);
    }

    public ResponseEntity update(int empId, EmployeePost employeePost){
        if(! validations.isEmpIdValid(empId)){
            return new ResponseEntity<>("employee not found",HttpStatus.BAD_REQUEST);
        }

        EmployeeInformation empToUpdate = employeeRepo.findByEmployeeId(empId);
        int mid = empToUpdate.getManagerId();
        if(employeePost.getManagerId()!=null){
            //check if manager id is valid
            if(validations.isEmpIdValid(mid)){
                //find the employee with given manager id
                EmployeeInformation temp=employeeRepo.findByEmployeeId(employeePost.getManagerId());
                //now find level of the employee with given manager id
                float levelOfNewManager = designationRepo.findByDesignation(temp.getDesignationId().getDesignation()).getLevel();

                //find level of current employee
                float currLevel =designationRepo.findByDesignation(empToUpdate.getDesignation()).getLevel();

                //if level of the employee is greater or equal to the level of manager
                if(currLevel > levelOfNewManager){
                    empToUpdate.setManagerId(mid);
                }
                else{
                    return new ResponseEntity<>("bad request",HttpStatus.BAD_REQUEST);
                }
            }
            else{
                return new ResponseEntity<>("employee does not exist",HttpStatus.NOT_FOUND);
            }
        }
        if(employeePost.getDesignationName()!=null){

            if(validations.isDesignationValid(employeePost.getDesignationName())){
                //get level of manager
                float manLevel = designationRepo.findByDesignation(employeeRepo.findByEmployeeId(mid).getDesignation()).getLevel();
                //get current level of employee
                float currLevel = designationRepo.findByDesignation(empToUpdate.getDesignation()).getLevel();
                //find the level of new designation
                float newLevel = designationRepo.findByDesignation(employeePost.getDesignationName()).getLevel();
                if(/*newLevel > currLevel*/ manLevel >= newLevel){
                    return new ResponseEntity<>("bad request",HttpStatus.BAD_REQUEST);
                }
                else if(!validations.isParentValid(empId)){
                    return new ResponseEntity("bad request",HttpStatus.BAD_REQUEST);
                }
                else{
                    empToUpdate.designationId = designationRepo.findByDesignation(employeePost.getDesignationName());
                }
            }
            else{
                return new ResponseEntity<>("designation not valid",HttpStatus.NOT_FOUND);
            }

        }
        if(employeePost.getEmpName()!=null){
            empToUpdate.setEmployeeName(employeePost.getEmpName());

        }
        //return updated information
        employeeRepo.save(empToUpdate);
        return new ResponseEntity<>(empToUpdate,HttpStatus.OK);
    }

    public ResponseEntity addAnEmployee(EmployeePost employeePost){
        EmployeeInformation empToAdd =new EmployeeInformation();
        //if there is no employee in the list, then first employee must be director
        if(employeeRepo.findAll().size() == 0){
            if(employeePost.getDesignationName().equalsIgnoreCase("Director")){
                if(employeePost.getManagerId() == null)
                {
                    //    empToAdd.setManagerId(null);
                    empToAdd.setEmployeeName(employeePost.getEmpName());
                    empToAdd.setDesignationId(designationRepo.findByDesignation(employeePost.getDesignationName()));
                    employeeRepo.save(empToAdd);
                }
                else{
                    return new ResponseEntity<>(employeePost, HttpStatus.BAD_REQUEST);
                }
            }
            else {
                return new ResponseEntity<>("first employee must be director",HttpStatus.BAD_REQUEST);
            }
        }
        else if(employeePost.getEmpName()!=null && employeePost.getDesignationName()!=null && employeePost.getManagerId()!=null && (!employeePost.getDesignationName().equalsIgnoreCase("director"))){
            //find employee to be manager
            EmployeeInformation temp = employeeRepo.findByEmployeeId(employeePost.getManagerId());
            //if employee to be manager is intern, then return
            if(temp.designationId.getDesignation().equalsIgnoreCase("Intern")){
                return new ResponseEntity<>("intern can not be manager",HttpStatus.BAD_REQUEST);
            }
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
            return new ResponseEntity<>("Invalid information",HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(empToAdd,HttpStatus.OK);

    }

    public ResponseEntity deleteAnEmployee(int empId){
        if(!validations.isEmpIdValid(empId)){
            return new ResponseEntity<>("employee not found",HttpStatus.BAD_REQUEST);
        }
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
