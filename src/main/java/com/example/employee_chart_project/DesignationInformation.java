package com.example.employee_chart_project;


import javax.persistence.*;

@Entity
@Table(name="designation_information")
public class DesignationInformation {

    @Id
    @Column(name = "designation_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int designationId;
    @Column(name = "designation")
    private String designation;
    @Column(name = "level")
    private int level;

    public int getDesignationId() {
        return designationId;
    }

    public void setDesignationId(int designationId) {
        this.designationId = designationId;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
