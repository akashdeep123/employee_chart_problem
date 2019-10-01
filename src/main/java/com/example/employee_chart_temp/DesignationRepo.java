package com.example.employee_chart_temp;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignationRepo extends CrudRepository<DesignationInformation, Integer> {
    DesignationInformation findByDesignation(String Designation);
}
