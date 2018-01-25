package com.xinshan.model.extend.salesTarget;

import com.xinshan.model.Position;
import com.xinshan.model.SalesTarget;
import com.xinshan.model.SalesTargetAmount;
import com.xinshan.model.SalesTargetAnalysis;

/**
 * Created by mxt on 17-8-22.
 */
public class SalesTargetAnalysisExtend extends SalesTargetAnalysis{
    private SalesTarget salesTarget;
    private SalesTargetAmount salesTargetAmount;
    private Position position;

    public SalesTarget getSalesTarget() {
        return salesTarget;
    }

    public void setSalesTarget(SalesTarget salesTarget) {
        this.salesTarget = salesTarget;
    }

    public SalesTargetAmount getSalesTargetAmount() {
        return salesTargetAmount;
    }

    public void setSalesTargetAmount(SalesTargetAmount salesTargetAmount) {
        this.salesTargetAmount = salesTargetAmount;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
