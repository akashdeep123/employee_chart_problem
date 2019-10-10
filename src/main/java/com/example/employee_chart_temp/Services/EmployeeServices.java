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

    public float returnLevel(int id){

        float level = designationRepo.findByDesignationIgnoreCase(employeeRepo.findByEmployeeId(id).getDesignation()).getLevel();

        return level;
    }

    public float returnLevel(String desg){
        float level = designationRepo.findByDesignationIgnoreCase(desg).getLevel();
        return level;
    }

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
        }

        EmployeeInformation emp = new EmployeeInformation();
        EmployeeInformation empToReplace = employeeRepo.findByEmployeeId(empId);


        //name, designation of the new employee must be provided
        if(employeePost.getEmpName()==null || employeePost.getDesignationName()==null){
            return new ResponseEntity<>("incomplete information", HttpStatus.BAD_REQUEST);
        }

        if(employeePost.getManagerId()==null){
            employeePost.setManagerId(empToReplace.getManagerId());
        }
        if(!validations.isDesignationValid(employeePost.getDesignationName().trim().toLowerCase())){
            return new ResponseEntity<>("designation invalid",HttpStatus.BAD_REQUEST);
        }

        //if director is to be replaced
        if(employeeRepo.findByEmployeeId(empId).getDesignation().equalsIgnoreCase("director")){
            if(!employeePost.getDesignationName().equalsIgnoreCase("director")){
                return new ResponseEntity<>("director can;t be replaced with employee of lower designation",HttpStatus.BAD_REQUEST);
            }

            emp.setEmployeeName(employeePost.getEmpName());
            emp.setDesignationId(designationRepo.findByDesignationIgnoreCase(employeePost.getDesignationName()));

            employeeRepo.save(emp);

            if(designationRepo.findByDesignationIgnoreCase(employeePost.getDesignationName()).getLevel() == designationRepo.findByDesignationIgnoreCase(empToReplace.getDesignation()).getLevel() ){

                employeeRepo.delete(empToReplace);
                List<EmployeeInformation> children = employeeRepo.findAllByManagerId(empId);
                for(EmployeeInformation em:children){
                    em.setManagerId(emp.getEmployeeId());
                    employeeRepo.save(em);
                }
                return new ResponseEntity<>(emp,HttpStatus.OK);
            }
            else {
                employeeRepo.delete(emp);
                return new ResponseEntity<>("bad request",HttpStatus.BAD_REQUEST);
            }

        }
        if(!validations.isEmpIdValid(employeePost.getManagerId())){
            return new ResponseEntity<>("manager not valid",HttpStatus.BAD_REQUEST);
        }
        if(validations.isParentChildRelation(employeePost,empId)){

            emp.setEmployeeName(employeePost.getEmpName());
            emp.setDesignationId(designationRepo.findByDesignationIgnoreCase(employeePost.getDesignationName()));
            emp.setManagerId(employeePost.getManagerId());

            employeeRepo.delete(empToReplace);
            employeeRepo.save(emp);

            List<EmployeeInformation> children = employeeRepo.findAllByManagerId(empId);
            for(EmployeeInformation em: children){
                em.setManagerId(emp.getEmployeeId());
                employeeRepo.save(em);
            }
        }
        else{
            return new ResponseEntity<>("invalid request",HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(emp,HttpStatus.OK);
    }

    public ResponseEntity update(int empId, EmployeePost employeePost){
        if(! validations.isEmpIdValid(empId)){
            return new ResponseEntity<>("employee not found",HttpStatus.BAD_REQUEST);
        }

        EmployeeInformation empToUpdate = employeeRepo.findByEmployeeId(empId);
        //start
        if(employeePost.getDesignationName()==null){
            employeePost.setDesignationName(empToUpdate.getDesignation());
        }
        if(employeePost.getManagerId()==null){
            employeePost.setManagerId(empToUpdate.getManagerId());
        }
        if(employeePost.getEmpName()==null){
            employeePost.setEmpName(empToUpdate.getEmployeeName());
        }
        if(!validations.isDesignationValid(employeePost.getDesignationName())){
            return new ResponseEntity<>("invalid request",HttpStatus.BAD_REQUEST);
        }
        //if employee to be updated is director
        if(employeeRepo.findByEmployeeId(empId).getDesignation().equalsIgnoreCase("director")){
            if(!employeePost.getDesignationName().equalsIgnoreCase("director")){
                return new ResponseEntity("bad request", HttpStatus.BAD_REQUEST);
            }
            if(employeePost.getManagerId()!=null){
                return new ResponseEntity("bad request", HttpStatus.BAD_REQUEST);
            }
            empToUpdate.setEmployeeName(employeePost.getEmpName());
            employeeRepo.save(empToUpdate);
            return new ResponseEntity(empToUpdate, HttpStatus.OK);
        }
        if(!validations.isEmpIdValid(employeePost.getManagerId())){
            return new ResponseEntity<>("invalid request",HttpStatus.BAD_REQUEST);
        }
        if(validations.isParentChildRelation(employeePost,empId)){
            empToUpdate.setManagerId(employeePost.getManagerId());
            empToUpdate.setDesignationId(designationRepo.findByDesignationIgnoreCase(employeePost.getDesignationName()));
            empToUpdate.setEmployeeName(employeePost.getEmpName());
            employeeRepo.save(empToUpdate);
            return new ResponseEntity<>(empToUpdate,HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("invalid request",HttpStatus.BAD_REQUEST);
        }

        //end

    }

    public ResponseEntity addAnEmployee(EmployeePost employeePost){
        if(employeePost.getEmpName()==null || employeePost.getDesignationName()==null ){
            return new ResponseEntity<>("please provide all data",HttpStatus.BAD_REQUEST);
        }
        EmployeeInformation empToAdd =new EmployeeInformation();
        //if there is no employee in the list, then first employee must be director
        if(employeeRepo.findAll().isEmpty()){
            if(employeePost.getDesignationName().equalsIgnoreCase("Director")){
                if(employeePost.getManagerId() == null)
                {
                    empToAdd.setEmployeeName(employeePost.getEmpName());
                    empToAdd.setDesignationId(designationRepo.findByDesignationIgnoreCase(employeePost.getDesignationName()));
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
        // name, designation, managerId of the new employee are required if employee to be added is not director.
        else if(employeePost.getEmpName()!=null && employeePost.getDesignationName()!=null && employeePost.getManagerId()!=null && (!employeePost.getDesignationName().equalsIgnoreCase("director"))){
                //find employee to be manager
                EmployeeInformation temp = employeeRepo.findByEmployeeId(employeePost.getManagerId());
                //if employee to be manager(of new employee) is intern, then return bad request
                if(temp.designationId.getDesignation().equalsIgnoreCase("Intern")){
                    return new ResponseEntity<>("intern can not be manager",HttpStatus.BAD_REQUEST);
                }
                //find level of manager
                float levelOfManager = returnLevel(temp.designationId.getDesignation().trim());//designationRepo.findByDesignationIgnoreCase(temp.designationId.getDesignation().trim()).getLevel();
                //find level of employee
                float empLevel = returnLevel(employeePost.getDesignationName().trim());//designationRepo.findByDesignationIgnoreCase(employeePost.getDesignationName().trim()).getLevel();
                if(levelOfManager >= empLevel){
                    return new ResponseEntity("bad request",HttpStatus.BAD_REQUEST);
                }
                else{
                    empToAdd.setEmployeeName(employeePost.getEmpName());
                    empToAdd.setManagerId(employeePost.getManagerId());
                    empToAdd.setDesignationId(designationRepo.findByDesignationIgnoreCase(employeePost.getDesignationName()));
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
