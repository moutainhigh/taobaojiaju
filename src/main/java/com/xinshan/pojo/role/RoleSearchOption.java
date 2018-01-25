package com.xinshan.pojo.role;

import com.xinshan.pojo.SearchOption;

import java.util.List;

/**
 * Created by mxt on 16-10-12.
 */
public class RoleSearchOption extends SearchOption {
    private List<Integer> roleIds;

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }
}
