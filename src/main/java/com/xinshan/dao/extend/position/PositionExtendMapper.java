package com.xinshan.dao.extend.position;

import com.xinshan.model.Position;
import com.xinshan.model.extend.position.PositionExtend;
import com.xinshan.pojo.position.PositionSearchOption;

import java.util.List;

/**
 * Created by mxt on 16-11-28.
 */
public interface PositionExtendMapper {
    void createPosition(Position position);

    List<PositionExtend> positionList(PositionSearchOption positionSearchOption);

    void updatePosition(Position position);

    void updatePositionDaogou(Position position);
}
