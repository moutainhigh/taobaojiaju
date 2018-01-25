package com.xinshan.components.role;

import com.xinshan.model.Employee;
import com.xinshan.model.extend.role.RoleExtend;
import com.xinshan.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by mxt on 16-10-12.
 */
@Component
public class RoleComponents {
    private final static Lock lock = new ReentrantLock();
    private static List<RoleExtend> list = new ArrayList<>();
    private static List<RoleExtend> roleTree = new ArrayList<>();//角色列表
    private static RoleService roleService;

    @Autowired
    public void setRoleService(RoleService roleService) {
        RoleComponents.roleService = roleService;
    }

    public static void clear() {
        lock.lock();
        try {
            list.clear();
            roleTree.clear();
        }finally {
            lock.unlock();
        }
    }

    public static List<RoleExtend> getRoleList() {
        if (list.isEmpty()) {
            init();
        }
        return list;
    }

    public static List<RoleExtend> getRoleTree() {
        if (roleTree.isEmpty()) {
            initRoleTree();
        }
        return roleTree;
    }

    private static void init() {
        lock.lock();
        try {
            list = roleService.roleList(null);
        } finally {
            lock.unlock();
        }
    }

    private static void initRoleTree() {
        lock.lock();
        try {
            roleTree();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static RoleExtend getRoleById(int role_id) {
        List<RoleExtend> list = getRoleList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getRole_id() == role_id) {
                return list.get(i);
            }
        }
        return null;
    }

    private static void roleTree() {
        List<RoleExtend> list = getRoleList();
        List<RoleExtend> roleExtends = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            RoleExtend roleExtend = list.get(i);
            int parent_role_id = roleExtend.getParent_role_id();
            if (parent_role_id == 0){
                roleExtend.setList(roleTree(roleExtend, list));
                roleExtends.add(roleExtend);
            }
        }
        RoleComponents.roleTree = roleExtends;
    }

    private static List<RoleExtend> roleTree(RoleExtend parentRole, List<RoleExtend> list) {
        List<RoleExtend> roleExtends = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            RoleExtend roleExtend = list.get(i);
            if (roleExtend.getParent_role_id() == parentRole.getRole_id()) {
                roleExtend.setList(roleTree(roleExtend, list));
                roleExtend.setParentRoleName(parentRole.getRole_name());
                roleExtends.add(roleExtend);
            }
        }
        return roleExtends;
    }

    public static List<RoleExtend> employeeRoles(Employee employee) {
        return roleService.roleListByRoleIds(employee.getRole_ids());
    }
}
