package com.xinshan.pojo.fastdfs;

/**
 * Created by jonson.xu on 6/24/15.
 */
public class DfsModel {
    private DfsFileInfo master;
    private Object slave;
    private Integer creator;
    private String permit;

    public DfsFileInfo getMaster() {
        return master;
    }

    public void setMaster(DfsFileInfo master) {
        this.master = master;
    }

    public Object getSlave() {
        return slave;
    }

    public void setSlave(Object slave) {
        this.slave = slave;
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public String getPermit() {
        return permit;
    }

    public void setPermit(String permit) {
        this.permit = permit;
    }
}
