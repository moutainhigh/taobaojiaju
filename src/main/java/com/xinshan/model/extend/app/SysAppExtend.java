package com.xinshan.model.extend.app;

import com.xinshan.model.SysApp;
import com.xinshan.model.SysAppButton;

import java.util.List;

/**
 * Created by mxt on 16-10-10.
 */
public class SysAppExtend extends SysApp {
    private List<SysAppExtend> apps;
    private List<SysAppButton> buttons;

    public List<SysAppExtend> getApps() {
        return apps;
    }

    public void setApps(List<SysAppExtend> apps) {
        this.apps = apps;
    }

    public List<SysAppButton> getButtons() {
        return buttons;
    }

    public void setButtons(List<SysAppButton> buttons) {
        this.buttons = buttons;
    }
}
