package com.example.employee_chart_temp.Controller;

import io.swagger.models.auth.In;

public class EmployeePut {
    private String employeeName = null;
    private String jobTitle = null;
    private Integer managerId = null;
    private boolean replace = false;

    public EmployeePut(){

    }

    public EmployeePut(String employeeName, String jobTitle, Integer managerId, boolean replace){

        this.employeeName = employeeName;
        this.jobTitle = jobTitle;
        this.managerId = managerId;
        this.replace = replace;

    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public boolean isReplace() {
        return replace;
    }

    public void setReplace(boolean replace) {
        this.replace = replace;
    }
}
