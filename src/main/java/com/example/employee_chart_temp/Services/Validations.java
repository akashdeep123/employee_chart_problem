package com.example.employee_chart_temp.Services;

import com.example.employee_chart_temp.Controller.EmployeePost;
import com.example.employee_chart_temp.Entities.EmployeeInformation;
import com.example.employee_chart_temp.Repositories.DesignationRepo;
import com.example.employee_chart_temp.Repositories.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Validations {
    @Autowired
    EmployeeRepo employeeRepo;
    @Autowired
    DesignationRepo designationRepo;
    @Autowired
    EmployeeServices empServices;

    public boolean isEmpIdValid(int empId){
        if(employeeRepo.findByEmployeeId(empId)!=null){ //if employee id exists.
            return true;
        }
        return false;
    }

    public boolean isDesignationValid(String desg){

        if(designationRepo.findByDesignationIgnoreCase(desg)!=null){
            return true;
        }
        return false;
    }


    public boolean isParentValid(int empId){
        List<EmployeeInformation> children=employeeRepo.findAllByManagerIdOrderByDesignationId_levelAsc(empId);
        if(children.isEmpty()){
            return true;
        }
        //System.out.println(children);
        EmployeeInformation lastChild = children.get(0);
        float levelOfParent = empServices.returnLevel(empId);//designationRepo.findByDesignationIgnoreCase(employeeRepo.findByEmployeeId(empId).getDesignation()).getLevel();
        float levelOfChild = empServices.returnLevel(lastChild.getDesignation());//designationRepo.findByDesignationIgnoreCase(lastChild.getDesignation()).getLevel();
        //System.out.println(levelOfChild+"   "+levelOfParent);
        if(levelOfParent < levelOfChild){
            return true;
        }
        return false;
    }
    public boolean isParentChildRelation(EmployeePost employeePost, int parId){
        float levelOfNewEmp = empServices.returnLevel(employeePost.getDesignationName().trim());//designationRepo.findByDesignationIgnoreCase(employeePost.getDesignationName()).getLevel();
        //Integer managerId=employeeRepo.findByEmployeeId(parId).getManagerId();
        //String desgOfManager = designationRepo.findByDesignation(employeeRepo.findByEmployeeId(managerId).getDesignation());
        float levelOfManager = empServices.returnLevel(employeePost.getManagerId());//designationRepo.findByDesignationIgnoreCase(employeeRepo.findByEmployeeId(managerId).getDesignation()).getLevel();
        if(levelOfManager < levelOfNewEmp){
            List<EmployeeInformation> children =employeeRepo.findAllByManagerIdOrderByDesignationId_levelAsc(parId);
            if(children.isEmpty()){
                return true;
            }
            EmployeeInformation lastChild = children.get(0);
            float levelOfLastChild = empServices.returnLevel(lastChild.getDesignation());//designationRepo.findByDesignationIgnoreCase(lastChild.getDesignation()).getLevel();
            System.out.println(levelOfLastChild+"   "+levelOfNewEmp);
            if(levelOfNewEmp < levelOfLastChild){
                return true;
            }
        }
        return false;
    }
    public boolean isParentChildRelation(EmployeeInformation emp, int parId){
        float levelOfNewEmp = empServices.returnLevel(emp.getDesignation().trim());//designationRepo.findByDesignationIgnoreCase(employeePost.getDesignationName()).getLevel();
        //Integer managerId=employeeRepo.findByEmployeeId(parId).getManagerId();
        //String desgOfManager = designationRepo.findByDesignation(employeeRepo.findByEmployeeId(managerId).getDesignation());
        float levelOfManager = empServices.returnLevel(emp.getManagerId());//designationRepo.findByDesignationIgnoreCase(employeeRepo.findByEmployeeId(managerId).getDesignation()).getLevel();
        if(levelOfManager < levelOfNewEmp){
            List<EmployeeInformation> children =employeeRepo.findAllByManagerIdOrderByDesignationId_levelAsc(parId);
            if(children.isEmpty()){
                return true;
            }
            EmployeeInformation lastChild = children.get(0);
            float levelOfLastChild = empServices.returnLevel(lastChild.getDesignation());//designationRepo.findByDesignationIgnoreCase(lastChild.getDesignation()).getLevel();
            if(levelOfNewEmp < levelOfLastChild){
                return true;
            }
        }
        return false;
    }

}
