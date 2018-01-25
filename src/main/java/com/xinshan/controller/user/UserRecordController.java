package com.xinshan.controller.user;

import com.xinshan.components.employee.EmployeeComponent;
import com.xinshan.model.*;
import com.xinshan.model.extend.user.UserRecordExtend;
import com.xinshan.service.UserRecordService;
import com.xinshan.utils.*;
import com.xinshan.pojo.user.UserSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by mxt on 16-11-3.
 */
@Controller
public class UserRecordController {
    @Autowired
    private UserRecordService userRecordService;

    /**
     * 添加编辑状态
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/user/workRecord/createStatus", "/user/workRecord/updateStatus"})
    public void createUserStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserStatus userStatus = Request2ModelUtils.covert(UserStatus.class, request);
        if (userStatus.getUser_status_id() == null) {
            userRecordService.createStatus(userStatus);
        }else {
            userRecordService.updateStatus(userStatus);
        }
        ResponseUtil.sendSuccessResponse(request, response, userStatus);
    }

    /**
     * 添加编辑销售阶段
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/user/workRecord/createStage","/user/workRecord/updateStage"})
    public void createUserStage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserStage userStage = Request2ModelUtils.covert(UserStage.class, request);
        if (userStage.getUser_stage_id() == null) {
            userRecordService.createStage(userStage);
        }else {
            userRecordService.updateStage(userStage);
        }
        ResponseUtil.sendSuccessResponse(request, response, userStage);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/user/workRecord/createSource", "/user/workRecord/updateSource"})
    public void createUserSource(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserSource userSource = Request2ModelUtils.covert(UserSource.class, request);
        if (userSource.getUser_source_id() == null) {
            userRecordService.createSource(userSource);
        }else {
            userRecordService.updateSource(userSource);
        }
        ResponseUtil.sendSuccessResponse(request, response, userSource);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/user/workRecord/createRecord", "/user/workRecord/updateRecord"})
    public void createUserRecord(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        UserRecord userRecord = Request2ModelUtils.covert(UserRecord.class, request);
        userRecord.setRecord_date(DateUtil.currentDate());
        userRecord.setRecord_employee_code(employee.getEmployee_code());
        userRecord.setRecord_employee_name(employee.getEmployee_name());
        if (userRecord.getUser_record_id() == null) {
            userRecordService.createRecord(userRecord);
        }else {
            userRecordService.updateRecord(userRecord);
        }

        ResponseUtil.sendSuccessResponse(request, response, userRecord);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/user/workRecord/workRecordList")
    public void userRecordList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = (Employee) request.getSession().getAttribute("employee");
        UserSearchOption userSearchOption = Request2ModelUtils.covert(UserSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(userSearchOption);
        if (userSearchOption.getAll() == null || userSearchOption.getAll() != 1) {
            //userSearchOption.setRecord_employee_code(employee.getEmployee_code());
            userSearchOption.setPermitEmployeeCodes(EmployeeComponent.employeePermit(employee));
        }
        List<UserRecordExtend> list = userRecordService.recordList(userSearchOption);
        Integer count = userRecordService.countRecord(userSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, userSearchOption);
    }

    @RequestMapping({"/user/workRecord/workStatusList"})
    public void userStatusList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseUtil.sendSuccessResponse(request, response, userRecordService.userStatuses(null));
    }
    @RequestMapping({"/user/workRecord/workStageList"})
    public void userStageList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseUtil.sendSuccessResponse(request, response, userRecordService.userStages(null));
    }
    @RequestMapping({"/user/workRecord/workSourceList"})
    public void userSourceList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseUtil.sendSuccessResponse(request, response, userRecordService.userSources(null));
    }
}
