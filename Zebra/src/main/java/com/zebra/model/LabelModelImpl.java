package com.zebra.model;
import com.zebra.contract.LabelContract;

/**
* Created by zhiPeng.S on 2016/12/01
*/

public class LabelModelImpl implements LabelContract.Model{
    private int id;
    private String content;

    public LabelModelImpl(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}