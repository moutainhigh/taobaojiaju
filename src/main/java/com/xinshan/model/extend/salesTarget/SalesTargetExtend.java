package com.xinshan.model.extend.salesTarget;

import com.xinshan.model.Position;
import com.xinshan.model.SalesTarget;
import com.xinshan.model.SalesTargetAmount;

import java.util.List;

/**
 * Created by mxt on 17-8-22.
 */
public class SalesTargetExtend extends SalesTarget {
    private Position position;
    private List<SalesTargetAmount> targetAmounts;

    public List<SalesTargetAmount> getTargetAmounts() {
        return targetAmounts;
    }

    public void setTargetAmounts(List<SalesTargetAmount> targetAmounts) {
        this.targetAmounts = targetAmounts;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
