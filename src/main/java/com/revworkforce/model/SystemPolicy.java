package com.revworkforce.model;

/**
 * Represents a system-wide configuration or policy.
 * Maps to the 'system_policies' table.
 * 
 * @author Gururaj Shetty
 */
public class SystemPolicy {

    private int policyId;
    private String policyName;
    private String policyValue;

    public int getPolicyId() {
        return policyId;
    }

    public void setPolicyId(int policyId) {
        this.policyId = policyId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyValue() {
        return policyValue;
    }

    public void setPolicyValue(String policyValue) {
        this.policyValue = policyValue;
    }
}
