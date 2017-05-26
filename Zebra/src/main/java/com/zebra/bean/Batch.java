package com.zebra.bean;

import com.sticker.app.model.Addon;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhiPeng.S on 2017/2/20.
 */

public class Batch implements Serializable {
    private List<Addon> addons;

    private int position;

    public List<Addon> getAddons() {
        return addons;
    }

    public void setAddons(List<Addon> addons) {
        this.addons = addons;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
