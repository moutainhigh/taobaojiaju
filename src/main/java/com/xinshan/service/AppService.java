package com.xinshan.service;

import com.xinshan.dao.SysAppButtonMapper;
import com.xinshan.dao.SysAppMapper;
import com.xinshan.dao.extend.app.AppExtendMapper;
import com.xinshan.model.SysApp;
import com.xinshan.model.SysAppButton;
import com.xinshan.model.extend.app.SysAppExtend;
import com.xinshan.pojo.app.AppSearchOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by mxt on 16-10-10.
 */
@Service
public class AppService {
    @Autowired
    private AppExtendMapper appExtendMapper;
    @Autowired
    private SysAppMapper sysAppMapper;
    @Autowired
    private SysAppButtonMapper sysAppButtonMapper;
    @Transactional
    public void createSysApp(SysApp sysApp) {
        appExtendMapper.createSysApp(sysApp);
    }
    @Transactional
    public void updateSysApp(SysApp sysApp) {
        appExtendMapper.updateSysApp(sysApp);
    }
    @Transactional
    public void createSysAppButton(SysAppButton sysAppButton) {
        appExtendMapper.createSysAppButton(sysAppButton);
    }
    @Transactional
    public void updateSysAppButton(SysAppButton sysAppButton) {
        appExtendMapper.updateSysAppButton(sysAppButton);
    }

    @Transactional
    public void appDelete(int app_id) {
        sysAppMapper.deleteByPrimaryKey(app_id);
    }
    @Transactional
    public void buttonDelete(int button_id) {
        sysAppButtonMapper.deleteByPrimaryKey(button_id);
    }

    public List<SysAppExtend> apps(AppSearchOption appSearchOption) {
        return appExtendMapper.apps(appSearchOption);
    }
    public List<SysAppButton> appButtons(AppSearchOption appSearchOption) {
        return appExtendMapper.buttons(appSearchOption);
    }

    public SysApp getSysAppById(int app_id) {
        return sysAppMapper.selectByPrimaryKey(app_id);
    }

    public SysAppButton getButtonById(int button_id) {
        return sysAppButtonMapper.selectByPrimaryKey(button_id);
    }

    public List<String> appPermitEmployees() {
        return appExtendMapper.appPermitEmployees();
    }

}
