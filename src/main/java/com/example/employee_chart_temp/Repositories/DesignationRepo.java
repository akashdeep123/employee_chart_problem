package com.example.employee_chart_temp.Repositories;

import com.example.employee_chart_temp.Entities.DesignationInformation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignationRepo extends CrudRepository<DesignationInformation, Integer> {
    DesignationInformation findByDesignationIgnoreCase(String Designation);
    DesignationInformation findBydesignationInformation(String did);

}
