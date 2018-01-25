package com.xinshan.components.app;

import com.xinshan.components.role.DataPermitComponents;
import com.xinshan.components.role.RoleComponents;
import com.xinshan.model.*;
import com.xinshan.model.extend.app.SysAppExtend;
import com.xinshan.model.extend.role.RoleExtend;
import com.xinshan.service.AppService;
import com.xinshan.service.RoleService;
import com.xinshan.utils.SplitUtils;
import com.xinshan.pojo.app.AppSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by mxt on 16-10-12.
 */
@Component
public class PermitComponents {
    //用户权限菜单
    private static Map<String, List<SysAppExtend>> employeeApp = new HashMap<>();
    //用户接口权限，权限控制
    private static Map<String, Set<String>> employeeUrl = new HashMap<>();
    private static List<PermitNocheck> noCheckUrl = new ArrayList<>();
    private static List<String> appPermitEmployees = new ArrayList<>();

    private final static Lock lock = new ReentrantLock();
    private static RoleService roleService;
    private static AppService appService;

    @Autowired
    public void setRoleService(RoleService roleService) {
        PermitComponents.roleService = roleService;
    }

    @Autowired
    public void setAppService(AppService appService) {
        PermitComponents.appService = appService;
    }


    public static void clear() {
        lock.lock();
        try {
            employeeApp.clear();
            employeeUrl.clear();
            noCheckUrl.clear();
            appPermitEmployees.clear();
        } finally {
            lock.unlock();
        }
    }

    public static void clear(String employee_code) {
        lock.lock();
        try {
            employeeApp.remove(employee_code);
            employeeUrl.remove(employee_code);
        } finally {
            lock.unlock();
        }
    }

    public static List<String> getAppPermitEmployees() {
        if (appPermitEmployees.isEmpty()) {
            initAppPermitEmployees();
        }
        return appPermitEmployees;
    }

    private static void initAppPermitEmployees(){
        lock.lock();
        try {
            appPermitEmployees = appService.appPermitEmployees();
        }finally {
            lock.unlock();
        }
    }

    public static List<PermitNocheck> getNoCheckUrl() {
        if (noCheckUrl == null || noCheckUrl.isEmpty()) {
            noCheckUrl = roleService.nocheckList();
        }
        return noCheckUrl;
    }

    public static List<SysAppExtend> getAppByEmployee(Employee employee) {
        if (!employeeApp.containsKey(employee.getEmployee_code())) {
            List<SysAppExtend> list = employeeApp(employee);
            putEmployeeApp(list, employee);
        }
        return employeeApp.get(employee.getEmployee_code());
    }

    public static Set<String> employeeUrls(Employee employee) {
        if (!employeeUrl.containsKey(employee.getEmployee_code())) {
            Set<String> set = employeeUrl(employee);
            employeeUrl.put(employee.getEmployee_code(), set);
        }
        return employeeUrl.get(employee.getEmployee_code());
    }

    public static void putEmployeeApp(List<SysAppExtend> list, Employee employee) {
        lock.lock();
        try {
            employeeApp.put(employee.getEmployee_code(), list);
            Set<String> set = employeeUrl(employee);
            employeeUrl.put(employee.getEmployee_code(), set);
        } finally {
            lock.unlock();
        }
    }

    /**
     *
     * @param list
     * @param baseUrl
     * @return
     */
    private static Set<String> employeeUrl(List<SysAppExtend> list, String baseUrl) {
        Set<String> set = new HashSet<>();
        for (int i = 0; i < list.size(); i++) {
            SysAppExtend sysAppExtend = list.get(i);
            String url = baseUrl + "/" +sysAppExtend.getApp_name();
            set.add(url);
            List<SysAppExtend> childrenApps = sysAppExtend.getApps();
            Set<String> childUrl = employeeUrl(childrenApps, url);
            set.addAll(childUrl);
            List<SysAppButton> buttons = sysAppExtend.getButtons();
            for (int j = 0; j < buttons.size(); j++) {
                SysAppButton button = buttons.get(j);
                set.addAll(buttonPermits(url, button.getButton_name()));
            }
        }
        return set;
    }

    /**
     * 用户接口权限
     * @param employee
     * @return
     */
    private static Set<String> employeeUrl(Employee employee) {
        Set<String> set = new TreeSet<>();
        List<RoleExtend> roles = RoleComponents.employeeRoles(employee);
        List<Integer> buttonIds = new ArrayList<>();
        for (int i = 0; i < roles.size(); i++) {
            RoleExtend role = roles.get(i);
            buttonIds.addAll(SplitUtils.splitToSet(role.getButton_ids(), ","));
        }
        List<SysAppButton> buttons = new ArrayList<>();
        if (buttonIds.size() > 0) {
            AppSearchOption appSearchOption = new AppSearchOption();
            appSearchOption.setButtonIds(buttonIds);
            buttons = appService.appButtons(appSearchOption);
        }

        boolean adminApp = false;//是否超级管理员
        if (getAppPermitEmployees().contains(employee.getEmployee_code())) {
            adminApp = true;
            buttons = appService.appButtons(null);
        }

        for (int i = 0; i < buttons.size(); i++) {
            SysAppButton button = buttons.get(i);
            int only_admin = button.getOnly_admin();//1仅有管理员可见,0所有可见
            if ((only_admin == 0) || (only_admin == 1 && adminApp)) {//全部可见 或 仅超级管理员可见
                String s = button.getButton_name();
                set.addAll(SplitUtils.splitToStrSet(s, ","));
            }
        }
        return set;
    }

    private static Set<String> buttonPermits(String url, String button_name){
        Set<String> set = new HashSet<>();
        List<String> list = SplitUtils.splitToStrList(button_name, ",");
        for(String ss: list) {
            set.add(url+"/"+ss);
        }
        return set;
    }

    private static List<SysAppExtend> employeeApp(Employee employee) {
        String role_ids = employee.getRole_ids();
        List<RoleExtend> roles = RoleComponents.employeeRoles(employee);
        List<Integer> app = new ArrayList<>();
        List<Integer> button = new ArrayList<>();
        for (int i = 0; i < roles.size(); i++) {
            Role role = roles.get(i);
            String appIds = role.getApp_ids();
            String buttonIds = role.getButton_ids();
            app.addAll(SplitUtils.splitToSet(appIds, ","));
            button.addAll(SplitUtils.splitToSet(buttonIds, ","));
        }
        if (app.size() == 0 && !getAppPermitEmployees().contains(employee.getEmployee_code())) {//没有任务菜单权限，且不是超级管理员
            return new ArrayList<>();
        }
        AppSearchOption appSearchOption = new AppSearchOption();
        if (app.size() > 0) {
            appSearchOption.setAppIds(app);
        }
        if (button.size() > 0) {
            appSearchOption.setButtonIds(button);
        }
        List<SysAppExtend> apps = appService.apps(appSearchOption);
        List<SysAppButton> buttons = appService.appButtons(appSearchOption);
        boolean adminApp = false;
        if (getAppPermitEmployees().contains(employee.getEmployee_code())) {
            adminApp = true;
        }
        if (adminApp) {
            apps = appService.apps(null);
            buttons = appService.appButtons(null);
        }
        List<SysAppExtend> list = AppComponents.appList(buttons, apps, adminApp, true);
        return list;
    }

    /**
     * 接口数据权限
     * @param employee
     * @param url
     * @return
     */
    public static Set<String> dataPermits(Employee employee, String url) {
        Set<String> set = new HashSet<>();
        Set<String> employeeUrls = employeeUrls(employee);
        if (!employeeUrls.contains(url)) {
            return set;
        }
        List<RoleExtend> roles = RoleComponents.employeeRoles(employee);
        for (int i = 0; i < roles.size(); i++) {
            RoleExtend roleExtend = roles.get(i);
            Integer role_id = roleExtend.getRole_id();
            DataPermit dataPermit = DataPermitComponents.dataPermit(role_id, url);
            if (dataPermit != null) {
                String data_cols = dataPermit.getData_cols();
                Set<String> strings = SplitUtils.splitToStrSet(data_cols, ",");
                set.addAll(strings);
            }
        }
        return set;
    }
}
