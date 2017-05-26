package com.zebra.bean;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/12/12.
 */

public class Category implements Serializable {

    /**
     * fname : 手机平板
     * fdi : 1017
     * headpic : http://192.168.1.188//common/images/fdiico/1002.png
     */

    private String fname;
    private int fdi;
    private String headpic;

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public int getFdi() {
        return fdi;
    }

    public void setFdi(int fdi) {
        this.fdi = fdi;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }
}
