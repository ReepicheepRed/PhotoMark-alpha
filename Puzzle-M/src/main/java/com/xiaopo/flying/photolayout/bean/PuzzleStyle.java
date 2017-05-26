package com.xiaopo.flying.photolayout.bean;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2017/2/28.
 */

public class PuzzleStyle implements Serializable {

    /**
     * id : 1
     * fdi : 2
     * filepath : http://192.168.1.188//images/FDITypes/2/xhdpi/1.png
     */

    private int id;
    private int fdi;
    private String filepath;
    private String themeid;
    private boolean isSelected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFdi() {
        return fdi;
    }

    public void setFdi(int fdi) {
        this.fdi = fdi;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getThemeid() {
        return themeid;
    }

    public void setThemeid(String themeid) {
        this.themeid = themeid;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
