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

    public boolean isEmpIdValid(int empId){
        if(employeeRepo.findByEmployeeId(empId)!=null){ //if employee id exists.
            return true;
        }
        return false;
    }

    public boolean isDesignationValid(String desg){
        if(designationRepo.findByDesignation(desg)!=null){
            return true;
        }
        return false;
    }


    public boolean isParentValid(int empId){
        List<EmployeeInformation> children=employeeRepo.findAllByManagerIdOrderByDesignationId_levelAsc(empId);
        if(children.isEmpty()){
            return true;
        }
        System.out.println(children);
        EmployeeInformation lastChild = children.get(0);
        float levelOfParent = designationRepo.findByDesignation(employeeRepo.findByEmployeeId(empId).getDesignation()).getLevel();
        float levelOfChild = designationRepo.findByDesignation(lastChild.getDesignation()).getLevel();
        //System.out.println(levelOfChild+"   "+levelOfParent);
        if(levelOfParent < levelOfChild){
            return true;
        }
        return false;
    }

    public boolean isParentChildRelation(EmployeePost employeePost, int empToReplace){
        float levelOfNewEmp = designationRepo.findByDesignation(employeePost.getDesignationName()).getLevel();
        Integer managerId=employeeRepo.findByEmployeeId(empToReplace).getManagerId();
        //String desgOfManager = designationRepo.findByDesignation(employeeRepo.findByEmployeeId(managerId).getDesignation());
        float levelOfManager = designationRepo.findByDesignation(employeeRepo.findByEmployeeId(managerId).getDesignation()).getLevel();
        if(levelOfManager < levelOfNewEmp){
            List<EmployeeInformation> children =employeeRepo.findAllByManagerIdOrderByDesignationId_levelAsc(empToReplace);
            if(children.isEmpty()){
                return true;
            }
            EmployeeInformation lastChild = children.get(0);
            float levelOfLastChild =designationRepo.findByDesignation(lastChild.getDesignation()).getLevel();
            if(levelOfNewEmp < levelOfLastChild){
                return true;
            }
        }
        return false;
    }

}
