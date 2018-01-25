package com.xinshan.service;

import com.xinshan.components.app.AppComponents;
import com.xinshan.dao.PermitNocheckMapper;
import com.xinshan.dao.RoleMapper;
import com.xinshan.dao.extend.role.RoleExtendMapper;
import com.xinshan.model.PermitNocheck;
import com.xinshan.model.Role;
import com.xinshan.model.SysApp;
import com.xinshan.model.SysAppButton;
import com.xinshan.model.extend.role.RoleExtend;
import com.xinshan.utils.SplitUtils;
import com.xinshan.pojo.role.RoleSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mxt on 16-10-10.
 */
@Service
public class RoleService {
    @Autowired
    private RoleExtendMapper roleExtendMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermitNocheckMapper permitNocheckMapper;

    @Transactional
    public void createRole(Role role) {
        roleExtendMapper.createRole(role);
    }
    @Transactional
    public void updateRole(Role role) {
        roleMapper.updateByPrimaryKey(role);
    }

    public List<PermitNocheck> nocheckList (){
        return permitNocheckMapper.selectAll();
    }

    public Role getRoleById(int role_id) {
        return roleMapper.selectByPrimaryKey(role_id);
    }

    @Transactional
    public void deleteRole(int role_id) {
        roleMapper.deleteByPrimaryKey(role_id);
    }

    public List<RoleExtend> roleList(RoleSearchOption roleSearchOption) {
        List<RoleExtend> list = roleExtendMapper.roleList(roleSearchOption);
        for (int i = 0; i < list.size(); i++) {
            RoleExtend roleExtend = list.get(i);
            String app_ids = roleExtend.getApp_ids();
            String button_ids = roleExtend.getButton_ids();
            roleExtend.setApp_titles(appTitles(app_ids));
            roleExtend.setButton_titles(buttonTitles(button_ids));
        }
        return list;
    }

    private String appTitles(String app_ids) {
        StringBuffer sb = new StringBuffer();
        List<Integer> appIds = SplitUtils.splitToList(app_ids, ",");
        for (int i = 0; i < appIds.size(); i++) {
            int app_id = appIds.get(i);
            SysApp sysApp = AppComponents.getAppById(app_id);
            sb.append(sysApp.getApp_title()).append(",");
        }
        return sb.toString();
    }

    private String buttonTitles(String button_ids) {
        StringBuffer sb = new StringBuffer();
        List<Integer> buttonIds = SplitUtils.splitToList(button_ids, ",");
        for (int i = 0; i < buttonIds.size(); i++) {
            int button_id = buttonIds.get(i);
            SysAppButton sysAppButton = AppComponents.getButtonById(button_id);
            if (sysAppButton != null) {
                sb.append(sysAppButton.getButton_title()).append(",");
            }
        }
        return sb.toString();
    }

    public List<RoleExtend> roleListByRoleIds(String roleIds) {
        RoleSearchOption roleSearchOption = new RoleSearchOption();
        roleSearchOption.setRoleIds(SplitUtils.splitToList(roleIds, ","));
        return roleList(roleSearchOption);
    }

    public Integer countRole(RoleSearchOption roleSearchOption) {
        return roleExtendMapper.countRole(roleSearchOption);
    }
}
