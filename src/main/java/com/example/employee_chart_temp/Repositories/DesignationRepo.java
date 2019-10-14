package com.example.employee_chart_temp.Repositories;

import com.example.employee_chart_temp.Entities.DesignationInformation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import sun.security.krb5.internal.crypto.Des;

import java.util.List;

@Repository
public interface DesignationRepo extends CrudRepository<DesignationInformation, Integer> {
    DesignationInformation findByDesignationIgnoreCase(String Designation);
    DesignationInformation findBydesignationInformation(String did);
    //DesignationInformation findByDesignation(String designation);

}
