package com.example.employee_chart_temp.Repositories;

import com.example.employee_chart_temp.Entities.EmployeeInformation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepo extends CrudRepository<EmployeeInformation, Integer> {
    List<EmployeeInformation> findAll();
    EmployeeInformation findByEmployeeId(int id);
    List<EmployeeInformation> findAllByManagerId(int mid);
    List<EmployeeInformation> findAllByOrderByDesignationId_levelAscEmployeeNameAsc();
    List<EmployeeInformation> findAllByManagerIdAndEmployeeIdIsNot(int parentId,int empId);
    //List<EmployeeInformation> findAllByManagerIdOrderByDesignationId_levl
    List<EmployeeInformation> findAllByManagerIdOrderByDesignationId_levelAsc(int id);

    EmployeeInformation findByManagerId(int mId);

}
