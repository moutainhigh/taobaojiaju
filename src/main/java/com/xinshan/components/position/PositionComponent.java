package com.xinshan.components.position;

import com.xinshan.model.Position;
import com.xinshan.model.extend.position.PositionExtend;
import com.xinshan.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mxt on 16-11-28.
 */
@Component
public class PositionComponent {

    private static PositionService positionService;
    @Autowired
    public void setPositionService(PositionService positionService) {
        PositionComponent.positionService = positionService;
    }

    private static List<PositionExtend> positionList = new ArrayList<>();//岗位列表
    private static List<PositionExtend> positions = new ArrayList<>();//岗位列表，包含下级

    public static void clear() {
        positions.clear();
        positionList.clear();
    }

    public static List<PositionExtend> getPositions() {
        if (positions == null || positions.isEmpty()) {
            init();
        }
        return positions;
    }

    public static List<PositionExtend> getPositionList() {
        if (positionList == null || positionList.isEmpty()) {
            positionList = positionService.positionList(null);
        }
        return positionList;
    }

    public static List<PositionExtend> getPositions(List<PositionExtend> list, int position_id) {
        for (int i = 0; i < list.size(); i++) {
            PositionExtend positionExtend = list.get(i);
            if (positionExtend.getPosition_id() == position_id) {
                return positionExtend.getPositions();
            } else {
              return getPositions(positionExtend.getPositions(), position_id);
            }
        }
        return null;
    }

    private static void init() {
        List<PositionExtend> list = getPositionList();
        List<PositionExtend> positionExtends = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            PositionExtend position = list.get(i);
            if (position.getPosition_parent_id() == null || position.getPosition_parent_id() == 0) {
                position.setPositions(childPosition(list, position));
                positionExtends.add(position);
            }
        }
        positions = positionExtends;
    }

    private static List<PositionExtend> childPosition(List<PositionExtend> list, Position position) {
        List<PositionExtend> positionList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            PositionExtend positionExtend = list.get(i);
            if (positionExtend.getPosition_parent_id() == position.getPosition_id()) {
                positionExtend.setPositions(childPosition(list, positionExtend));
                positionList.add(positionExtend);
            }
        }
        return positionList;
    }

    public static List<Integer> childPositionIds(List<PositionExtend> list, Position position) {
        List<Integer> positionIds = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            PositionExtend positionExtend = list.get(i);
            if (positionExtend.getPosition_parent_id() == position.getPosition_id()) {
                positionIds.add(positionExtend.getPosition_id());
            }
        }
        return positionIds;
    }

    public static PositionExtend getPositionById(int position_id) {
        List<PositionExtend> list = getPositionList();
        for (int i = 0; i < list.size(); i++) {
            PositionExtend position = list.get(i);
            if (position.getPosition_id() == position_id) {
                return position;
            }
        }
        return null;
    }

    public static PositionExtend getParentPosition(int parent_position_id) {
        List<PositionExtend> list = getPositionList();
        for (int i = 0; i < list.size(); i++) {
            PositionExtend positionExtend = list.get(i);
            if (positionExtend.getPosition_id() == parent_position_id) {
                return positionExtend;
            }
        }
        return null;
    }

    public static String childPositionIds(int position_id) {
        PositionExtend positionExtend = getPositionExtend(getPositions(), position_id);
        if (positionExtend.getPositions() != null) {
            return childPositionIds(positionExtend.getPositions());
        }else {
            return positionExtend.getPosition_id()+"";
        }
    }

    private static String childPositionIds(List<PositionExtend> list) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            PositionExtend position = list.get(i);
            buffer.append(position.getPosition_id()).append(",");
            if (position.getPositions() != null) {
                String s = childPositionIds(position.getPositions());
                buffer.append(s);
            }
        }
        return buffer.toString();
    }

    public static PositionExtend getPositionExtend(List<PositionExtend> list, int position_id) {
        for (int i = 0; i < list.size(); i++) {
            PositionExtend positionExtend = list.get(i);
            if (positionExtend.getPosition_id() == position_id) {
                return positionExtend;
            }else {
                PositionExtend position = getPositionExtend(positionExtend.getPositions(), position_id);
                if (position != null) {
                    return position;
                }
            }
        }
        return null;
    }
}
