package com.example.employee_chart_project;

import org.springframework.data.repository.CrudRepository;

public interface EmployeeRepo extends CrudRepository<EmployeeInformation, Integer> {

}
