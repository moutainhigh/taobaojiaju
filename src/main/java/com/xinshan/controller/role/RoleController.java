package com.xinshan.controller.role;

import com.xinshan.components.app.AppComponents;
import com.xinshan.components.orderFee.OrderFeeComponents;
import com.xinshan.components.role.RoleComponents;
import com.xinshan.model.Role;
import com.xinshan.model.extend.role.RoleExtend;
import com.xinshan.service.RoleService;
import com.xinshan.utils.Request2ModelUtils;
import com.xinshan.utils.ResponseUtil;
import com.xinshan.utils.SearchOptionUtil;
import com.xinshan.utils.SplitUtils;
import com.xinshan.pojo.role.RoleSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by mxt on 16-10-10.
 */
@Controller
public class RoleController {
    @Autowired
    private RoleService roleService;

    @RequestMapping({"/sys/role/createRole","/sys/role/updateRole"})
    public void createRole(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Role role = Request2ModelUtils.covert(Role.class, request);
        String appIds = role.getApp_ids();
        String buttonIds = role.getButton_ids();
        Set<Integer> set = SplitUtils.splitToSet(appIds, ",");//去掉重复
        Set<Integer> s = new HashSet<>();
        for(int appId : set){
            Set<Integer> parentAppId = AppComponents.parentAppIds(appId);
            s.addAll(parentAppId);
        }
        set.addAll(s);
        appIds = set.toString();
        appIds = appIds.substring(1, appIds.length() - 1);
        role.setApp_ids(appIds);
        set = SplitUtils.splitToSet(buttonIds, ",");
        buttonIds = set.toString();
        buttonIds = buttonIds.substring(1, buttonIds.length() - 1);
        role.setButton_ids(buttonIds);
        if (role.getParent_role_id() == null) {
            role.setParent_role_id(0);
        }
        if (role.getRole_id() == null) {
            roleService.createRole(role);
        }else {
            roleService.updateRole(role);
        }
        ResponseUtil.sendSuccessResponse(request, response);
        AppComponents.clear();//重置权限
        RoleComponents.clear();
        OrderFeeComponents.clear();
    }

    @RequestMapping("/sys/role/roleDetail")
    public void roleDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int role_id = Integer.parseInt(request.getParameter("role_id"));
        RoleExtend roleExtend = RoleComponents.getRoleById(role_id);
        if (roleExtend.getParent_role_id() != null && roleExtend.getParent_role_id() > 0) {
            roleExtend.setParentRoleName(RoleComponents.getRoleById(roleExtend.getParent_role_id()).getRole_name());
        }
        ResponseUtil.sendSuccessResponse(request, response, roleExtend);
    }

    @RequestMapping("/sys/role/roleDelete")
    public void roleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int role_id = Integer.parseInt(request.getParameter("role_id"));
        roleService.deleteRole(role_id);
        ResponseUtil.sendSuccessResponse(request, response);
        AppComponents.clear();//重置权限
    }

    /**
     * 角色列表
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping({"/sys/role/list", "/sys/employee/roleList"})//
    public void roleList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RoleSearchOption roleSearchOption = Request2ModelUtils.covert(RoleSearchOption.class, request);
        SearchOptionUtil.getSearchOptionUtil().searchOptionInit(roleSearchOption);
        List<RoleExtend> list = RoleComponents.getRoleTree();
        ResponseUtil.sendSuccessResponse(request, response, list);
    }

    /**
     * 修改角色权限
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/sys/role/permit")
    public void rolePermit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String appIds = request.getParameter("role_ids");
        String buttonIds = request.getParameter("sys_app_button_ids");
        int roleId = Integer.parseInt(request.getParameter("role_id"));
        Role role = roleService.getRoleById(roleId);
        Set<Integer> set = SplitUtils.splitToSet(appIds, ",");//去掉重复
        appIds = set.toString();
        Iterator<Integer> iterator = set.iterator();
        while (iterator.hasNext()) {//
            int appId = iterator.next();
            Set<Integer> parentAppId = AppComponents.parentAppIds(appId);
            set.addAll(parentAppId);
        }
        appIds = appIds.substring(1, appIds.length() - 1);
        role.setApp_ids(appIds);
        set = SplitUtils.splitToSet(buttonIds, ",");
        buttonIds = set.toString();
        buttonIds = buttonIds.substring(1, buttonIds.length() - 1);
        role.setButton_ids(buttonIds);
        roleService.updateRole(role);
        ResponseUtil.sendSuccessResponse(request, response);
    }
}
