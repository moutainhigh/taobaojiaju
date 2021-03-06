package com.xinshan.model;

public class SupplierSeries {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column supplier_series.supplier_series_id
     *
     * @mbggenerated
     */
    private Integer supplier_series_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column supplier_series.series_name
     *
     * @mbggenerated
     */
    private String series_name;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column supplier_series.supplier_id
     *
     * @mbggenerated
     */
    private Integer supplier_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column supplier_series.series_status
     *
     * @mbggenerated
     */
    private Integer series_status;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column supplier_series.series_desc
     *
     * @mbggenerated
     */
    private String series_desc;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column supplier_series.supplier_series_id
     *
     * @return the value of supplier_series.supplier_series_id
     *
     * @mbggenerated
     */
    public Integer getSupplier_series_id() {
        return supplier_series_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column supplier_series.supplier_series_id
     *
     * @param supplier_series_id the value for supplier_series.supplier_series_id
     *
     * @mbggenerated
     */
    public void setSupplier_series_id(Integer supplier_series_id) {
        this.supplier_series_id = supplier_series_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column supplier_series.series_name
     *
     * @return the value of supplier_series.series_name
     *
     * @mbggenerated
     */
    public String getSeries_name() {
        return series_name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column supplier_series.series_name
     *
     * @param series_name the value for supplier_series.series_name
     *
     * @mbggenerated
     */
    public void setSeries_name(String series_name) {
        this.series_name = series_name == null ? null : series_name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column supplier_series.supplier_id
     *
     * @return the value of supplier_series.supplier_id
     *
     * @mbggenerated
     */
    public Integer getSupplier_id() {
        return supplier_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column supplier_series.supplier_id
     *
     * @param supplier_id the value for supplier_series.supplier_id
     *
     * @mbggenerated
     */
    public void setSupplier_id(Integer supplier_id) {
        this.supplier_id = supplier_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column supplier_series.series_status
     *
     * @return the value of supplier_series.series_status
     *
     * @mbggenerated
     */
    public Integer getSeries_status() {
        return series_status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column supplier_series.series_status
     *
     * @param series_status the value for supplier_series.series_status
     *
     * @mbggenerated
     */
    public void setSeries_status(Integer series_status) {
        this.series_status = series_status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column supplier_series.series_desc
     *
     * @return the value of supplier_series.series_desc
     *
     * @mbggenerated
     */
    public String getSeries_desc() {
        return series_desc;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column supplier_series.series_desc
     *
     * @param series_desc the value for supplier_series.series_desc
     *
     * @mbggenerated
     */
    public void setSeries_desc(String series_desc) {
        this.series_desc = series_desc == null ? null : series_desc.trim();
    }
}