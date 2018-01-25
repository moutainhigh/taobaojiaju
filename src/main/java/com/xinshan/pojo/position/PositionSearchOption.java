package com.xinshan.pojo.position;

import com.xinshan.pojo.SearchOption;

import java.util.List;

/**
 * Created by mxt on 16-11-28.
 */
public class PositionSearchOption extends SearchOption {
    private List<Integer> positionList;
    private Integer daogou;

    public Integer getDaogou() {
        return daogou;
    }

    public void setDaogou(Integer daogou) {
        this.daogou = daogou;
    }

    public List<Integer> getPositionList() {
        return positionList;
    }

    public void setPositionList(List<Integer> positionList) {
        this.positionList = positionList;
    }
}
