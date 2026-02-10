package com.revworkforce.model;

import java.sql.Timestamp;

/**
 * Represents a job designation (e.g., "Senior Developer", "HR Manager").
 * Maps to the 'designations' table.
 * 
 * @author Gururaj Shetty
 */
public class Designation {

    private int designationId;
    private String designationName;

    public int getDesignationId() {
        return designationId;
    }

    public void setDesignationId(int designationId) {
        this.designationId = designationId;
    }

    public String getDesignationName() {
        return designationName;
    }

    public void setDesignationName(String designationName) {
        this.designationName = designationName;
    }
}
