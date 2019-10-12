package com.example.employee_chart_temp.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import sun.security.krb5.internal.crypto.Des;

import javax.persistence.*;

@Entity
@Table(name = "designation_information")
public class DesignationInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int designationInformation;

    private String designation;

    private float level;

    public int getdesignationInformation() {
        return designationInformation;
    }

    public void setdesignationInformation(int designationInformation) {
        this.designationInformation = designationInformation;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
    }
}
