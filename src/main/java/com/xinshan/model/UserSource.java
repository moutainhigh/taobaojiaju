package com.xinshan.model;

public class UserSource {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_source.user_source_id
     *
     * @mbggenerated
     */
    private Integer user_source_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_source.user_source_name
     *
     * @mbggenerated
     */
    private String user_source_name;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_source.user_source_id
     *
     * @return the value of user_source.user_source_id
     *
     * @mbggenerated
     */
    public Integer getUser_source_id() {
        return user_source_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_source.user_source_id
     *
     * @param user_source_id the value for user_source.user_source_id
     *
     * @mbggenerated
     */
    public void setUser_source_id(Integer user_source_id) {
        this.user_source_id = user_source_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_source.user_source_name
     *
     * @return the value of user_source.user_source_name
     *
     * @mbggenerated
     */
    public String getUser_source_name() {
        return user_source_name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_source.user_source_name
     *
     * @param user_source_name the value for user_source.user_source_name
     *
     * @mbggenerated
     */
    public void setUser_source_name(String user_source_name) {
        this.user_source_name = user_source_name == null ? null : user_source_name.trim();
    }
}