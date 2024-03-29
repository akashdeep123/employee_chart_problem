package com.example.employee_chart_temp.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import javax.persistence.*;

@Entity
@Table(name = "employee_information")
public class EmployeeInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private int employeeId;

    @JsonProperty("name")
    private String employeeName;


    @Transient
    @JsonProperty("jobTitle")
    //private String Designation;

    public String getDesignation() {
        return this.designationInformation.getDesignation();
    }

    @OneToOne
    @JoinColumn(name = "designation_id")

    @JsonIgnore
    public DesignationInformation designationInformation;

    @Nullable
    @JsonProperty("manager")
    private Integer managerId;

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

    public DesignationInformation getdesignationInformation() {
        return designationInformation;
    }

    public void setdesignationInformation(DesignationInformation designationInformation) {
        this.designationInformation = designationInformation;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }
}
