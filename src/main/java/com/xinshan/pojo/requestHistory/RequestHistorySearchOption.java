package com.xinshan.pojo.requestHistory;

import com.xinshan.pojo.SearchOption;

/**
 * Created by mxt on 17-3-30.
 */
public class RequestHistorySearchOption extends SearchOption{
    private String request_url;
    private Integer print_commodity_id;
    private Integer order_id;
    private String request_param;


    private String query_request_data;

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public String getQuery_request_data() {
        return query_request_data;
    }

    public void setQuery_request_data(String query_request_data) {
        this.query_request_data = query_request_data;
    }

    public Integer getPrint_commodity_id() {
        return print_commodity_id;
    }

    public void setPrint_commodity_id(Integer print_commodity_id) {
        this.print_commodity_id = print_commodity_id;
    }

    public String getRequest_param() {
        return request_param;
    }

    public void setRequest_param(String request_param) {
        this.request_param = request_param;
    }

    public String getRequest_url() {
        return request_url;
    }

    public void setRequest_url(String request_url) {
        this.request_url = request_url;
    }
}
