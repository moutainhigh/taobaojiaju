package com.xinshan.model;

public class RoleAdmin {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column role_admin.role_admin_id
     *
     * @mbggenerated
     */
    private Integer role_admin_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column role_admin.employee_code
     *
     * @mbggenerated
     */
    private String employee_code;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column role_admin.role_admin_id
     *
     * @return the value of role_admin.role_admin_id
     *
     * @mbggenerated
     */
    public Integer getRole_admin_id() {
        return role_admin_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column role_admin.role_admin_id
     *
     * @param role_admin_id the value for role_admin.role_admin_id
     *
     * @mbggenerated
     */
    public void setRole_admin_id(Integer role_admin_id) {
        this.role_admin_id = role_admin_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column role_admin.employee_code
     *
     * @return the value of role_admin.employee_code
     *
     * @mbggenerated
     */
    public String getEmployee_code() {
        return employee_code;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column role_admin.employee_code
     *
     * @param employee_code the value for role_admin.employee_code
     *
     * @mbggenerated
     */
    public void setEmployee_code(String employee_code) {
        this.employee_code = employee_code == null ? null : employee_code.trim();
    }
}