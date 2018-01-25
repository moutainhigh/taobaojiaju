package com.xinshan.model.extend.position;

import com.xinshan.model.Position;

import java.util.List;

/**
 * Created by mxt on 16-11-28.
 */
public class PositionExtend extends Position {
    private List<PositionExtend> positions;
    private String parent_name;

    public String getParent_name() {
        return parent_name;
    }

    public void setParent_name(String parent_name) {
        this.parent_name = parent_name;
    }

    public List<PositionExtend> getPositions() {
        return positions;
    }

    public void setPositions(List<PositionExtend> positions) {
        this.positions = positions;
    }
}
