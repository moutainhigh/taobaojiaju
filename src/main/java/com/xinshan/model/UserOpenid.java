package com.xinshan.model;

public class UserOpenid {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_openid.user_openid_id
     *
     * @mbggenerated
     */
    private Integer user_openid_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_openid.user_id
     *
     * @mbggenerated
     */
    private Integer user_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_openid.openid
     *
     * @mbggenerated
     */
    private String openid;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_openid.user_openid_id
     *
     * @return the value of user_openid.user_openid_id
     *
     * @mbggenerated
     */
    public Integer getUser_openid_id() {
        return user_openid_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_openid.user_openid_id
     *
     * @param user_openid_id the value for user_openid.user_openid_id
     *
     * @mbggenerated
     */
    public void setUser_openid_id(Integer user_openid_id) {
        this.user_openid_id = user_openid_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_openid.user_id
     *
     * @return the value of user_openid.user_id
     *
     * @mbggenerated
     */
    public Integer getUser_id() {
        return user_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_openid.user_id
     *
     * @param user_id the value for user_openid.user_id
     *
     * @mbggenerated
     */
    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_openid.openid
     *
     * @return the value of user_openid.openid
     *
     * @mbggenerated
     */
    public String getOpenid() {
        return openid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_openid.openid
     *
     * @param openid the value for user_openid.openid
     *
     * @mbggenerated
     */
    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }
}