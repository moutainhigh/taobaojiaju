package com.xinshan.model;

public class UserStatus {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_status.user_status_id
     *
     * @mbggenerated
     */
    private Integer user_status_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_status.user_status_name
     *
     * @mbggenerated
     */
    private String user_status_name;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_status.user_status_id
     *
     * @return the value of user_status.user_status_id
     *
     * @mbggenerated
     */
    public Integer getUser_status_id() {
        return user_status_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_status.user_status_id
     *
     * @param user_status_id the value for user_status.user_status_id
     *
     * @mbggenerated
     */
    public void setUser_status_id(Integer user_status_id) {
        this.user_status_id = user_status_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_status.user_status_name
     *
     * @return the value of user_status.user_status_name
     *
     * @mbggenerated
     */
    public String getUser_status_name() {
        return user_status_name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_status.user_status_name
     *
     * @param user_status_name the value for user_status.user_status_name
     *
     * @mbggenerated
     */
    public void setUser_status_name(String user_status_name) {
        this.user_status_name = user_status_name == null ? null : user_status_name.trim();
    }
}