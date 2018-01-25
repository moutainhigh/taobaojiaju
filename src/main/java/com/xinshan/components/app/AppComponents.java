package com.xinshan.components.app;

import com.xinshan.model.SysApp;
import com.xinshan.model.SysAppButton;
import com.xinshan.model.extend.app.SysAppExtend;
import com.xinshan.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by mxt on 16-10-10.
 */
@Component
public class AppComponents {
    private static List<SysAppExtend> list = new ArrayList<>();//菜单列表去除菜单设置,树行结构
    private static List<SysAppExtend> adminList = new ArrayList<>();//全部菜单列表,树行结构
    private static List<SysAppExtend> sysApps = new ArrayList<>();//全部菜单列表，原始列表
    private static List<SysAppButton> sysAppButtons = new ArrayList<>();

    private static AppService appService;

    @Autowired
    public void setAppService(AppService appService) {
        AppComponents.appService = appService;
    }

    public static void clear() {
        try {
            list.clear();
            sysApps.clear();
            sysAppButtons.clear();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<SysAppExtend> getList(String employee_code) {
        if (list == null || list.isEmpty() || adminList== null || adminList.isEmpty()) {
            init();
        }
        if (PermitComponents.getAppPermitEmployees().contains(employee_code)) {
            return adminList;
        }else {
            return list;
        }
    }

    public static List<SysAppExtend> getSysApps() {
        if (sysApps == null || sysApps.isEmpty()) {
            init();
        }
        return sysApps;
    }

    public static List<SysAppButton> getSysAppButtons() {
        if (sysAppButtons == null || sysAppButtons.isEmpty()) {
            init();
        }
        return sysAppButtons;
    }

    private static void init() {
        sysAppButtons = appService.appButtons(null);
        sysApps = appService.apps(null);
        List<SysAppExtend> list = appList(sysAppButtons, sysApps, false, false);
        AppComponents.list = list;
        AppComponents.adminList = appList(sysAppButtons, sysApps, true, false);
        PermitComponents.clear();
    }

    /**
     *
     * @param buttons
     * @param apps
     * @param adminApp  超级管理员
     * @param status 需不需要判断状态
     * @return
     */
    public static List<SysAppExtend> appList(List<SysAppButton> buttons, List<SysAppExtend> apps, boolean adminApp, boolean status) {
        List<SysAppExtend> list = new ArrayList<>();
        for (int i = 0; i < apps.size(); i++) {
            SysApp sysApp = apps.get(i);
            if (sysApp.getParent_app() == 0) {//第一级
                SysAppExtend sysAppExtend = initSysAppExtend(sysApp);
                int app_status = sysAppExtend.getApp_status();
                int only_admin = sysAppExtend.getOnly_admin();
                if ((status && app_status == 1) || !status) {//需要判断状态
                    if ((only_admin == 0) || (only_admin == 1 && adminApp)){//全部可见 或 仅超级管理员可见
                        list.add(sysAppExtend);
                        sysAppExtend.setButtons(getButtonsByAppId(sysAppExtend.getApp_id(), buttons, adminApp));
                        List<SysAppExtend> sysAppExtends = getChildApps(sysAppExtend, apps, buttons, adminApp, status);
                        Collections.sort(sysAppExtends, new SysAppSort());
                        sysAppExtend.setApps(sysAppExtends);
                    }
                }
            }
        }
        //TODO 排序
        Collections.sort(list, new SysAppSort());
        return list;
    }

    private static SysAppExtend initSysAppExtend(SysApp sysApp) {
        SysAppExtend sysAppExtend = new SysAppExtend();
        sysAppExtend.setApp_id(sysApp.getApp_id());
        sysAppExtend.setApp_icon(sysApp.getApp_icon());
        sysAppExtend.setApp_name(sysApp.getApp_name());
        sysAppExtend.setApp_sort(sysApp.getApp_sort());
        sysAppExtend.setApp_status(sysApp.getApp_status());
        sysAppExtend.setApp_title(sysApp.getApp_title());
        sysAppExtend.setLinks(sysApp.getLinks());
        sysAppExtend.setParent_app(sysApp.getParent_app());
        sysAppExtend.setOnly_admin(sysApp.getOnly_admin());
        sysAppExtend.setJs_directory(sysApp.getJs_directory());
        return sysAppExtend;
    }

    private static List<SysAppExtend> getChildApps(SysApp sysApp, List<SysAppExtend> apps, List<SysAppButton> buttons, boolean appAdmin, boolean status) {
        List<SysAppExtend> list = new ArrayList<>();
        for (int i = 0; i < apps.size(); i++) {
            SysApp app = apps.get(i);
            if (app.getParent_app() == sysApp.getApp_id()) {
                int only_admin = app.getOnly_admin();
                int app_status = app.getApp_status();
                if ((status && app_status == 1) || !status) {//需要判断状态
                    if ((only_admin == 0) || (only_admin == 1 && appAdmin)) {//全部可见 或 仅超级管理员可见
                        SysAppExtend sysAppExtend = initSysAppExtend(app);
                        sysAppExtend.setButtons(getButtonsByAppId(app.getApp_id(), buttons, appAdmin));
                        List<SysAppExtend> sysAppExtends = getChildApps(sysAppExtend, apps, buttons, appAdmin, status);
                        Collections.sort(sysAppExtends, new SysAppSort());
                        sysAppExtend.setApps(sysAppExtends);
                        list.add(sysAppExtend);
                    }
                }
            }
        }
        return list;
    }

    public static Set<Integer> parentAppIds(int appId) {
        SysApp sysApp = appService.getSysAppById(appId);
        Set<Integer> set = new HashSet<>();
        if (sysApp.getParent_app() == 0) {
            return set;
        }
        List<SysAppExtend> list = appService.apps(null);
        List<SysAppExtend> apps = parentApp(sysApp, list);
        for (int i = 0; i < apps.size(); i++) {
            SysAppExtend app = apps.get(i);
            set.add(app.getApp_id());
        }
        return set;
    }

    private static List<SysAppExtend> parentApp(SysApp sysApp, List<SysAppExtend> list) {
        List<SysAppExtend> apps = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            SysAppExtend sysAppExtend = list.get(i);
            if (sysAppExtend.getApp_id() == sysApp.getParent_app()) {
                apps.add(sysAppExtend);
                apps.addAll(parentApp(sysAppExtend, list));
            }
        }
        return apps;
    }

    private static List<SysAppButton> getButtonsByAppId(int appId, List<SysAppButton> buttons, boolean appAdmin) {
        List<SysAppButton> list = new ArrayList<>();
        for (int i = 0; i < buttons.size(); i++) {
            SysAppButton button = buttons.get(i);
            if (button.getApp_id() == appId) {
                if (button.getOnly_admin() == 0) {
                    list.add(button);
                }else {
                    if (appAdmin) {
                        list.add(button);
                    }
                }
            }
        }
        return list;
    }

    public static SysAppExtend getAppById(int app_id) {
        List<SysAppExtend> list = getSysApps();
        for (int i = 0; i < list.size(); i++) {
            SysAppExtend sysApp = list.get(i);
            if (sysApp.getApp_id() == app_id) {
                return sysApp;
            }
        }
        return null;
    }

    public static SysAppButton getButtonById(int button_id) {
        List<SysAppButton> list = getSysAppButtons();
        for (int i = 0; i < list.size(); i++) {
            SysAppButton sysAppButton = list.get(i);
            if (sysAppButton.getSys_app_button_id() == button_id) {
                return sysAppButton;
            }
        }
        return null;
    }

    private static class SysAppSort implements Comparator<SysApp>{
        @Override
        public int compare(SysApp o1, SysApp o2) {
            if (o1 == null) {
                return -1;
            }else if (o2 == null) {
                return 1;
            }else {
                int sort1 = o1.getApp_sort();
                int sort2 = o2.getApp_sort();
                if (sort1 == sort2) {
                    return o1.getApp_id() - o2.getApp_id();
                }else {
                    return sort1 - sort2;
                }
            }
        }
    }
}
