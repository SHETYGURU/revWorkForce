package com.revworkforce.model;

/**
 * Represents a Role (Employee, Manager, Admin).
 * Maps to the 'roles' table.
 */
public class Role {

    private Integer roleId;
    private String roleName;

    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
}
