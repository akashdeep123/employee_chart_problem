package com.example.employee_chart_temp.Services;

import com.example.employee_chart_temp.Entities.EmployeeInformation;
import com.example.employee_chart_temp.Repositories.DesignationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Validations {

    @Autowired
    DesignationRepo drepo;

    public boolean isListEmpty(List<EmployeeInformation> list){
        if(list.size()==0){
            return true;
        }
        return false;
    }

    public boolean isRelationCorrect(String managerDesg, String subDesg){
        if(drepo.findByDesignation(managerDesg).getLevel() < drepo.findByDesignation(subDesg).getLevel()){
            return true;
        }
        return false;
    }

}
