package com.example.employee_chart_temp.Services;

import com.example.employee_chart_temp.Controller.EmployeeEntity;
import com.example.employee_chart_temp.Entities.DesignationInformation;
import com.example.employee_chart_temp.Entities.EmployeeInformation;
import com.example.employee_chart_temp.Repositories.DesignationRepo;
import com.example.employee_chart_temp.Repositories.EmployeeRepo;
import com.example.employee_chart_temp.Util.MessageUtil;
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
    @Autowired
    MessageUtil messageUtil;

    public float returnLevel(int id){

        float level = designationRepo.findByDesignationIgnoreCase(employeeRepo.findByEmployeeId(id).getDesignation()).getLevel();

        return level;
    }

    public float returnLevel(String desg){
        float level = designationRepo.findByDesignationIgnoreCase(desg).getLevel();
        return level;
    }

    public ResponseEntity returnAllEmployee(){
        List<EmployeeInformation> employees = employeeRepo.findAllByOrderByDesignationInformation_levelAscEmployeeNameAsc();
        if(employees.size()>0)
            return new ResponseEntity<>(employees, HttpStatus.OK);
        else
            return new ResponseEntity<>(messageUtil.getMessage("EMPTY_DATABASE"),HttpStatus.NOT_FOUND);
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
        map.put("employee",employeeInformation);

        if(employeeInformation.getManagerId()!=null){
            manager=employeeRepo.findByEmployeeId(employeeInformation.getManagerId());
            map.put("manager",manager);

            //colleagues=employeeRepo.findAllByManagerIdAndEmployeeIdIsNot(employeeInformation.getManagerId(),employeeInformation.getEmployeeId());
            colleagues=employeeRepo.findAllByManagerIdAndEmployeeIdIsNotOrderByDesignationInformation_levelAscEmployeeNameAsc(employeeInformation.getManagerId(),employeeInformation.getEmployeeId());
            if(colleagues!=null)
                map.put("colleagues",colleagues);
        }


        //List<EmployeeInformation> reportingToo =employeeRepo.findAllByManagerIdAndEmployeeIdIsNot(employeeInformation.getEmployeeId(),employeeInformation.getEmployeeId());
        List<EmployeeInformation> reportingToo = employeeRepo.findAllByManagerIdAndEmployeeIdIsNotOrderByDesignationInformation_levelAscEmployeeNameAsc(employeeInformation.getEmployeeId(),employeeInformation.getEmployeeId());
        if(reportingToo!=null)
            map.put("subordinates",reportingToo);

        return map;

    }

    public ResponseEntity replace(int empId, EmployeeEntity employeeEntity){

        if(! validations.isEmpIdValid(empId)){
            return new ResponseEntity<>(messageUtil.getMessage("EMPLOYEE_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }

        if(employeeEntity.getJobTitle().trim().equals("")){
            return new ResponseEntity(messageUtil.getMessage("JOB_TITLE_BLANK"), HttpStatus.BAD_REQUEST);
        }

        if(employeeEntity.getName().trim().equals("")){
            return new ResponseEntity(messageUtil.getMessage("EMPLOYEE_NAME_BLANK"),HttpStatus.BAD_REQUEST);
        }

        EmployeeInformation emp = new EmployeeInformation();
        EmployeeInformation empToReplace = employeeRepo.findByEmployeeId(empId);


        //name, designation of the new employee must be provided
        if(employeeEntity.getName()==null || employeeEntity.getJobTitle()==null){
            return new ResponseEntity<>(messageUtil.getMessage("INCOMPLETE_INFORMATION"), HttpStatus.BAD_REQUEST);
        }

        if (!validations.validateLetters(employeeEntity.getName())){
            return new ResponseEntity(messageUtil.getMessage("INVALID_NAME"),HttpStatus.BAD_REQUEST);
        }



        if(employeeEntity.getManagerId()==null){
            employeeEntity.setManagerId(empToReplace.getManagerId());
        }
//        if(employeeEntity.getManagerId()==-1){
//            employeeEntity.setManagerId(null);
//        }
        if(!validations.isDesignationValid(employeeEntity.getJobTitle().trim().toLowerCase())){
            return new ResponseEntity<>(messageUtil.getMessage("INVALID_DESIGNATION"),HttpStatus.BAD_REQUEST);
        }

        //if director is to be replaced
        if(employeeRepo.findByEmployeeId(empId).getDesignation().equalsIgnoreCase("director")){
            if(!employeeEntity.getJobTitle().equalsIgnoreCase("director")){
                return new ResponseEntity<>("director can't be replaced with employee of lower designation",HttpStatus.BAD_REQUEST);
            }

            emp.setEmployeeName(employeeEntity.getName());
            emp.setdesignationInformation(designationRepo.findByDesignationIgnoreCase(employeeEntity.getJobTitle()));

            employeeRepo.save(emp);

            if(designationRepo.findByDesignationIgnoreCase(employeeEntity.getJobTitle()).getLevel() == designationRepo.findByDesignationIgnoreCase(empToReplace.getDesignation()).getLevel() ){

                employeeRepo.delete(empToReplace);
                List<EmployeeInformation> children = employeeRepo.findAllByManagerId(empId);
                for(EmployeeInformation em:children){
                    em.setManagerId(emp.getEmployeeId());
                    employeeRepo.save(em);
                }
                return new ResponseEntity<>(get(emp.getEmployeeId()),HttpStatus.OK);
            }
            else {
                employeeRepo.delete(emp);
                return new ResponseEntity<>("bad request",HttpStatus.BAD_REQUEST);
            }

        }
        if(!validations.isEmpIdValid(employeeEntity.getManagerId())){
            return new ResponseEntity<>("manager not valid",HttpStatus.BAD_REQUEST);
        }
        if(validations.isParentChildRelation(employeeEntity,empId)){

            emp.setEmployeeName(employeeEntity.getName());
            emp.setdesignationInformation(designationRepo.findByDesignationIgnoreCase(employeeEntity.getJobTitle()));
            emp.setManagerId(employeeEntity.getManagerId());

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

        return new ResponseEntity<>(get(emp.getEmployeeId()),HttpStatus.OK);
    }

    public ResponseEntity update(int empId, EmployeeEntity employeeEntity){
        if(employeeEntity.getManagerId()==null && employeeEntity.getJobTitle()==null && employeeEntity.getName()==null){
            return new ResponseEntity<>(messageUtil.getMessage("INCOMPLETE_INFORMATION"),HttpStatus.BAD_REQUEST);
        }
        if(employeeEntity.getManagerId()!=null && employeeEntity.getManagerId()==-1){
            employeeEntity.setManagerId(null);
        }
        if(! validations.isEmpIdValid(empId)){
            return new ResponseEntity<>(messageUtil.getMessage("EMPLOYEE_NOT_FOUND"),HttpStatus.BAD_REQUEST);
        }
//
//        if(EmployeeEntity.getJobTitle().trim().equals("")){
//            return new ResponseEntity(messageUtil.getMessage("JOB_TITLE_BLANK"), HttpStatus.BAD_REQUEST);
//        }
//
//        if(EmployeeEntity.getEmployeeName().trim().equals("")){
//            return new ResponseEntity(messageUtil.getMessage("EMPLOYEE_NAME_BLANK"),HttpStatus.BAD_REQUEST);
//        }

        EmployeeInformation empToUpdate = employeeRepo.findByEmployeeId(empId);
        //start
        if(employeeEntity.getJobTitle()==null){
            employeeEntity.setJobTitle(empToUpdate.getDesignation());
        }
        if(employeeEntity.getManagerId()==null){
            employeeEntity.setManagerId(empToUpdate.getManagerId());
        }
        if(employeeEntity.getName()==null){
            employeeEntity.setName(empToUpdate.getEmployeeName());
        }
        if(!validations.validateLetters(employeeEntity.getName())){
            return new ResponseEntity<>(messageUtil.getMessage("INVALID_NAME"),HttpStatus.BAD_REQUEST);
        }
        if(!validations.isDesignationValid(employeeEntity.getJobTitle())){
            return new ResponseEntity<>("invalid request",HttpStatus.BAD_REQUEST);
        }
        //if employee to be updated is director
        if(employeeRepo.findByEmployeeId(empId).getDesignation().equalsIgnoreCase("director")){
            if(!employeeEntity.getJobTitle().equalsIgnoreCase("director")){
                return new ResponseEntity("bad request", HttpStatus.BAD_REQUEST);
            }
            if(employeeEntity.getManagerId()!=null){
                return new ResponseEntity("bad request", HttpStatus.BAD_REQUEST);
            }
            empToUpdate.setEmployeeName(employeeEntity.getName());
            employeeRepo.save(empToUpdate);
            return new ResponseEntity(get(empToUpdate.getEmployeeId()), HttpStatus.OK);
        }
        if(!validations.isEmpIdValid(employeeEntity.getManagerId())){
            return new ResponseEntity<>("invalid request",HttpStatus.BAD_REQUEST);
        }
        if(validations.isParentChildRelation(employeeEntity,empId)){
            empToUpdate.setManagerId(employeeEntity.getManagerId());
            empToUpdate.setdesignationInformation(designationRepo.findByDesignationIgnoreCase(employeeEntity.getJobTitle()));
            empToUpdate.setEmployeeName(employeeEntity.getName());
            employeeRepo.save(empToUpdate);
            return new ResponseEntity<>(get(empToUpdate.getEmployeeId()),HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("invalid request",HttpStatus.BAD_REQUEST);
        }

        //end

    }

    public ResponseEntity addAnEmployee(EmployeeEntity employeeEntity){

        if(employeeEntity.getName()==null || employeeEntity.getJobTitle()==null ){
            return new ResponseEntity<>(messageUtil.getMessage("INCOMPLETE_INFORMATION"),HttpStatus.BAD_REQUEST);
        }

        String desgEmployeeEntity = employeeEntity.getJobTitle();
        if(designationRepo.findByDesignationIgnoreCase(desgEmployeeEntity)==null){
            return new ResponseEntity<>(messageUtil.getMessage("INVALID_DESIGNATION"),HttpStatus.BAD_REQUEST);
        }

        String nameEmployeeEntity = employeeEntity.getName();
        if(!validations.validateLetters(nameEmployeeEntity)){
            return new ResponseEntity<>(messageUtil.getMessage("INVALID_NAME"),HttpStatus.BAD_REQUEST);
        }


        if(!employeeEntity.getJobTitle().equalsIgnoreCase("director")){
            if(employeeEntity.getManagerId()==null){
                return new ResponseEntity<>(messageUtil.getMessage("MANAGER_ID_BLANK"),HttpStatus.BAD_REQUEST);
            }
        }

        if(employeeEntity.getManagerId()==-1){
            employeeEntity.setManagerId(null);
        }

        if(employeeEntity.getJobTitle().trim().equals("")){
            return new ResponseEntity(messageUtil.getMessage("JOB_TITLE_BLANK"), HttpStatus.BAD_REQUEST);
        }

        if(employeeEntity.getName().trim().equals("")){
            return new ResponseEntity(messageUtil.getMessage("EMPLOYEE_NAME_BLANK"),HttpStatus.BAD_REQUEST);
        }

        EmployeeInformation empToAdd =new EmployeeInformation();
        //if there is no employee in the list, then first employee must be director
        if(employeeRepo.findAll().isEmpty()){
            if(employeeEntity.getJobTitle().equalsIgnoreCase("Director")){
                if(employeeEntity.getManagerId() == null)
                {
                    empToAdd.setEmployeeName(employeeEntity.getName());
                    empToAdd.setdesignationInformation(designationRepo.findByDesignationIgnoreCase(employeeEntity.getJobTitle()));
                    employeeRepo.save(empToAdd);
                }
                else{
                    return new ResponseEntity<>(employeeRepo.findByEmployeeId(empToAdd.getEmployeeId()), HttpStatus.BAD_REQUEST);
                }
            }
            else {
                return new ResponseEntity<>("first employee must be director",HttpStatus.BAD_REQUEST);
            }
        }
        // name, designation, managerId of the new employee are required if employee to be added is not director.
        else if(employeeEntity.getName()!=null && employeeEntity.getJobTitle()!=null && employeeEntity.getManagerId()!=null && (!employeeEntity.getJobTitle().equalsIgnoreCase("director"))){
                //find employee to be manager
            if(!validations.isEmpIdValid(employeeEntity.getManagerId())){
                return new ResponseEntity<>("invalid request",HttpStatus.BAD_REQUEST);
            }
                EmployeeInformation temp = employeeRepo.findByEmployeeId(employeeEntity.getManagerId());
                //if employee to be manager(of new employee) is intern, then return bad request
                if(temp.designationInformation.getDesignation().equalsIgnoreCase("Intern")){
                    return new ResponseEntity<>("intern can not be manager",HttpStatus.BAD_REQUEST);
                }
                //find level of manager
                float levelOfManager = returnLevel(temp.designationInformation.getDesignation().trim());//designationRepo.findByDesignationIgnoreCase(temp.designationInformation.getDesignation().trim()).getLevel();
                //find level of employee
                float empLevel = returnLevel(employeeEntity.getJobTitle().trim());//designationRepo.findByDesignationIgnoreCase(employeePost.getDesignationName().trim()).getLevel();
                if(levelOfManager >= empLevel){
                    return new ResponseEntity("bad request",HttpStatus.BAD_REQUEST);
                }
                else{
                    empToAdd.setEmployeeName(employeeEntity.getName());
                    empToAdd.setManagerId(employeeEntity.getManagerId());
                    empToAdd.setdesignationInformation(designationRepo.findByDesignationIgnoreCase(employeeEntity.getJobTitle()));
                    employeeRepo.save(empToAdd);
                }

        }
        else{
            return new ResponseEntity<>("Invalid information",HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(employeeRepo.findByEmployeeId(empToAdd.getEmployeeId()),HttpStatus.CREATED);

    }

    public ResponseEntity deleteAnEmployee(int empId){
        if (empId<0){
            return new ResponseEntity<>(messageUtil.getMessage("INVALID_ID"),HttpStatus.BAD_REQUEST);
        }
        if(!validations.isEmpIdValid(empId)){
            return new ResponseEntity<>(messageUtil.getMessage("EMPLOYEE_NOT_FOUND"),HttpStatus.NOT_FOUND);
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
            return new ResponseEntity<>(empToDelete,HttpStatus.NO_CONTENT);
        }

    }
}
