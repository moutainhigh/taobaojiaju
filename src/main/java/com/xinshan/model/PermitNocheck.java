package com.xinshan.model;

public class PermitNocheck {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column permit_nocheck.permit_nocheck_id
     *
     * @mbggenerated
     */
    private Integer permit_nocheck_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column permit_nocheck.url
     *
     * @mbggenerated
     */
    private String url;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column permit_nocheck.permit_nocheck_id
     *
     * @return the value of permit_nocheck.permit_nocheck_id
     *
     * @mbggenerated
     */
    public Integer getPermit_nocheck_id() {
        return permit_nocheck_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column permit_nocheck.permit_nocheck_id
     *
     * @param permit_nocheck_id the value for permit_nocheck.permit_nocheck_id
     *
     * @mbggenerated
     */
    public void setPermit_nocheck_id(Integer permit_nocheck_id) {
        this.permit_nocheck_id = permit_nocheck_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column permit_nocheck.url
     *
     * @return the value of permit_nocheck.url
     *
     * @mbggenerated
     */
    public String getUrl() {
        return url;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column permit_nocheck.url
     *
     * @param url the value for permit_nocheck.url
     *
     * @mbggenerated
     */
    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }
}