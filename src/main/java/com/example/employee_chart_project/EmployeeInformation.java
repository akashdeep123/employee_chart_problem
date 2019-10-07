package com.example.employee_chart_project;

import javax.persistence.*;

@Entity
@Table(name="employee_information")
public class EmployeeInformation {

    @Id
    @Column(name = "employee_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int employeeId;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "designation_id")
    private int designationId;

    @Column(name = "manager_id")
    private int managerId;

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public int getDesignationId() {
        return designationId;
    }

    public void setDesignationId(int designationId) {
        this.designationId = designationId;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    @Override
    public String toString() {
        return "EmployeeInformation{" +
                "employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", designationId=" + designationId +
                ", managerId=" + managerId +
                '}';
    }
}
