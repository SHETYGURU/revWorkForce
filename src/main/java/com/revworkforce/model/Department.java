package com.revworkforce.model;

import java.sql.Timestamp;

/**
 * Represents a Department.
 * Maps to the 'departments' table.
 * 
 * @author Gururaj Shetty
 */
public class Department {

    private Integer departmentId;
    private String departmentName;
    private Timestamp createdAt;

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
