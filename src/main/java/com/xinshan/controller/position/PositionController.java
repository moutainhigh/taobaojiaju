package com.xinshan.controller.position;

import com.xinshan.components.position.PositionComponent;
import com.xinshan.model.Position;
import com.xinshan.model.extend.position.PositionExtend;
import com.xinshan.service.PositionService;
import com.xinshan.utils.Request2ModelUtils;
import com.xinshan.utils.ResponseUtil;
import com.xinshan.utils.SplitUtils;
import com.xinshan.pojo.position.PositionSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by mxt on 16-11-28.
 */
@Controller
public class PositionController {
    @Autowired
    private PositionService positionService;

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/position/createPosition")
    public void createPosition(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Position position = Request2ModelUtils.covert(Position.class, request);
        position.setDaogou(0);
        positionService.createPosition(position);
        PositionComponent.clear();
        ResponseUtil.sendSuccessResponse(request, response, position);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/position/updatePosition")
    public void updatePosition(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Position position = Request2ModelUtils.covert(Position.class, request);
        positionService.updatePosition(position);
        PositionComponent.clear();
        ResponseUtil.sendSuccessResponse(request, response, position);
    }

    @RequestMapping("/sys/position/updatePositionVisible")
    public void updatePositionVisible(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int position_id = Integer.parseInt(request.getParameter("position_id"));
            String visible_position_ids = request.getParameter("visible_position_ids");
        PositionExtend positionExtend = PositionComponent.getPositionById(position_id);
        positionExtend.setVisible_position_ids(visible_position_ids);
        positionService.updatePosition(positionExtend);
        PositionComponent.clear();
        ResponseUtil.sendSuccessResponse(request, response, positionExtend);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/sys/position/positionList", "/sys/employee/positionList", "/order/order/positionList"})
    public void positionList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getParameter("position_id") != null) {
            ResponseUtil.sendSuccessResponse(request, response,
                    PositionComponent.getPositions(PositionComponent.getPositions(), Integer.parseInt(request.getParameter("position"))));
        }else {
            ResponseUtil.sendSuccessResponse(request, response, PositionComponent.getPositions());
        }
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/position/positionDetail")
    public void positionDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int position_id = Integer.parseInt(request.getParameter("position_id"));
        PositionExtend positionExtend = positionService.getPositionExtendById(position_id);
        String visible_position_ids = positionExtend.getVisible_position_ids();
        if (visible_position_ids != null && !"".equals(visible_position_ids)) {
            PositionSearchOption positionSearchOption = new PositionSearchOption();
            positionSearchOption.setPositionList(SplitUtils.splitToList(visible_position_ids, ","));
            positionExtend.setPositions(positionService.positionList(positionSearchOption));
        }
        if (positionExtend.getPosition_parent_id() != null || positionExtend.getPosition_parent_id() != 0) {
            PositionExtend parentPosition = PositionComponent.getParentPosition(positionExtend.getPosition_parent_id());
            if (parentPosition != null) {
                positionExtend.setParent_name(parentPosition.getPosition_name());
            }
        }

        ResponseUtil.sendSuccessResponse(request, response, positionExtend);
    }
}
