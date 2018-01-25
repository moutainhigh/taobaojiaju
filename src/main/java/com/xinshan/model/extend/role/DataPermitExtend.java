package com.xinshan.model.extend.role;

import com.xinshan.model.DataPermit;
import com.xinshan.model.Role;

/**
 * Created by mxt on 17-5-5.
 */
public class DataPermitExtend extends DataPermit {
    private Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
