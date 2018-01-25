package com.xinshan.controller.role;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xinshan.components.app.PermitComponents;
import com.xinshan.components.employee.EmployeeComponent;
import com.xinshan.components.role.RoleComponents;
import com.xinshan.model.DataPermit;
import com.xinshan.model.Employee;
import com.xinshan.model.Role;
import com.xinshan.model.extend.role.DataPermitExtend;
import com.xinshan.model.extend.role.RoleExtend;
import com.xinshan.pojo.role.DataPermitSearchOption;
import com.xinshan.service.DataPermitService;
import com.xinshan.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by mxt on 17-5-5.
 */
@Controller
public class DataPermitController {

    @Autowired
    private DataPermitService dataPermitService;

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/dataPermit/createDataPermit")
    public void createDataPermit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DataPermit dataPermit = Request2ModelUtils.covert(DataPermit.class, request);
        dataPermitService.createDataPermit(dataPermit);
        ResponseUtil.sendSuccessResponse(request, response, dataPermit);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/dataPermit/updateDatePermit")
    public void updateDataPermit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DataPermit dataPermit = Request2ModelUtils.covert(DataPermit.class, request);
        dataPermitService.updateDataPermit(dataPermit);
        ResponseUtil.sendSuccessResponse(request, response);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/dataPermit/dataPermitDetail")
    public void dataPermitDetail(HttpServletRequest request, HttpServletResponse response)throws IOException {
        DataPermitSearchOption dataPermitSearchOption = Request2ModelUtils.covert(DataPermitSearchOption.class, request);
        List<DataPermit> list = dataPermitService.dataPermitList(dataPermitSearchOption);
        if (list != null && list.size() == 1) {
            DataPermit dataPermit = list.get(0);
            DataPermitExtend dataPermitExtend = new DataPermitExtend();
            dataPermitExtend.setData_cols(dataPermit.getData_cols());
            dataPermitExtend.setUrl(dataPermit.getUrl());
            dataPermitExtend.setRole_id(dataPermit.getRole_id());
            dataPermitExtend.setData_permit_id(dataPermit.getData_permit_id());
            RoleExtend roleById = RoleComponents.getRoleById(dataPermit.getRole_id());
            Role role = new Role();
            role.setRole_name(roleById.getRole_name());
            dataPermitExtend.setRole(role);
            ResponseUtil.sendSuccessResponse(request, response, dataPermitExtend);
        }else {
            ResponseUtil.sendSuccessResponse(request, response);
        }
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/dataPermit/dataPermitList")
    public void dataPermitList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DataPermitSearchOption dataPermitSearchOption = Request2ModelUtils.covert(DataPermitSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(dataPermitSearchOption);
        List<DataPermit> list = dataPermitService.dataPermitList(dataPermitSearchOption);
        Integer count = dataPermitService.countDataPermit(dataPermitSearchOption);
        ResponseUtil.sendListResponse(request, response, list, count, dataPermitSearchOption);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/dataPermit/permitCols")
    public void dataPermit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String url = request.getParameter("url");
        Employee employee = RequestUtils.getRequestUtils().getEmployeeCode(request);
        if (employee == null) {
            ResponseUtil.sendResponse(request, response, new ResultData("0x0004", "没有登录"));
            return;
        }
        employee = EmployeeComponent.getEmployeeByCode(employee.getEmployee_code());
        Set<String> set = PermitComponents.dataPermits(employee, url);
        ResponseUtil.sendSuccessResponse(request, response, set);
    }
}
