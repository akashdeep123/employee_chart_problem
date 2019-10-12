package com.example.employee_chart_temp.Controller;

public class EmployeeEntity {
    //private Integer empId=null;

    private String employeeName=null;
    private Integer managerId=null;
    private String jobTitle=null;
    private boolean replace=false;

    public EmployeeEntity(){

    }

    public EmployeeEntity(String employeeName, String jobTitle, Integer managerId){
        this.employeeName = employeeName;
        this.jobTitle =jobTitle;
        this.managerId = managerId;
    }

    public EmployeeEntity(String employeeName, String jobTitle, Integer managerId, boolean replace){
        this.replace = replace;
        this.employeeName = employeeName;
        this.jobTitle =jobTitle;
        this.managerId = managerId;
    }

    public boolean isReplace() {
        return replace;
    }

    public void setReplace(boolean replace) {
        this.replace = replace;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String empName) {
        this.employeeName = empName;
    }

//    public Integer getEmpId() {
//        return empId;
//    }
//
//    public void setEmpId(int empId) {
//        this.empId = empId;
//    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
}
