package com.example.employee_chart_temp;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepo extends CrudRepository<EmployeeInformation, Integer> {
    List<EmployeeInformation> findAll();
    EmployeeInformation findByEmployeeId(int id);
    List<EmployeeInformation> findAllByManagerIdAndEmployeeIdIsNot(int parentId,int empId);
}
