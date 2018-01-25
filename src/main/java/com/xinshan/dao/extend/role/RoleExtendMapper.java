package com.xinshan.dao.extend.role;

import com.xinshan.model.Role;
import com.xinshan.model.extend.role.RoleExtend;
import com.xinshan.pojo.role.RoleSearchOption;

import java.util.List;

/**
 * Created by mxt on 16-10-10.
 */
public interface RoleExtendMapper {
    void createRole(Role role);

    List<RoleExtend> roleList(RoleSearchOption roleSearchOption);

    Integer countRole(RoleSearchOption roleSearchOption);
}
