package com.xinshan.service;

import com.xinshan.dao.PositionMapper;
import com.xinshan.dao.extend.position.PositionExtendMapper;
import com.xinshan.model.Position;
import com.xinshan.model.extend.position.PositionExtend;
import com.xinshan.pojo.position.PositionSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mxt on 16-11-28.
 */
@Service
public class PositionService {
    @Autowired
    private PositionExtendMapper positionExtendMapper;
    @Autowired
    private PositionMapper positionMapper;

    @Transactional
    public void createPosition(Position position) {
        positionExtendMapper.createPosition(position);
    }

    @Transactional
    public void updatePosition(Position position) {
        positionExtendMapper.updatePosition(position);
    }

    @Transactional
    public void updatePositionDaogou(List<PositionExtend> list) {
        PositionSearchOption positionSearchOption = new PositionSearchOption();
        positionSearchOption.setDaogou(1);
        List<PositionExtend> positions = positionExtendMapper.positionList(positionSearchOption);
        for (int i = 0; i < positions.size(); i++) {
            PositionExtend position = positions.get(i);
            position.setDaogou(0);
            positionExtendMapper.updatePositionDaogou(position);
        }
        for (int i = 0; i < list.size(); i++) {
            PositionExtend position = list.get(i);
            position.setDaogou(1);
            positionExtendMapper.updatePositionDaogou(position);
        }
    }

    public List<PositionExtend> positionList(PositionSearchOption positionSearchOption) {
        return positionExtendMapper.positionList(positionSearchOption);
    }

    public PositionExtend getPositionExtendById(int position_id) {
        PositionSearchOption positionSearchOption = new PositionSearchOption();
        List<Integer> positionIdList = new ArrayList<>();
        positionIdList.add(position_id);
        positionSearchOption.setPositionList(positionIdList);
        List<PositionExtend> list = positionExtendMapper.positionList(positionSearchOption);
        if (list != null && list.size() >0){
            return list.get(0);
        }
        return null;
    }
}
