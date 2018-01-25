package com.xinshan.dao.extend.app;

import com.xinshan.model.SysApp;
import com.xinshan.model.SysAppButton;
import com.xinshan.model.extend.app.SysAppExtend;
import com.xinshan.pojo.app.AppSearchOption;

import java.util.List;

/**
 * Created by mxt on 16-10-10.
 */
public interface AppExtendMapper {

    void createSysApp(SysApp sysApp);

    void createSysAppButton(SysAppButton sysAppButton);
    void updateSysAppButton(SysAppButton sysAppButton);

    List<SysAppExtend> apps(AppSearchOption appSearchOption);

    List<SysAppButton> buttons(AppSearchOption appSearchOption);

    List<String> appPermitEmployees();

    void updateSysApp(SysApp sysApp);
}
