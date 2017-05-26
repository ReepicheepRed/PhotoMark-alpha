package com.zebra.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/12/14.
 */
@Table(name = "photomark")
public class Template2 implements Serializable {

    @Column(name = "id", isId = true)
    private int id;

    @Column(name = "fdi")
    private int fdi;

    @Column(name = "category")
    private int category;

    @Column(name = "num")
    private int num;

    @Column(name = "content")
    private String content;

    @Column(name = "imgPath")
    private String imgPath;

    @Column(name = "oriPath")
    private String oriPath;

    @Column(name = "samPath")
    private List<String> samPath;

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

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getOriPath() {
        return oriPath;
    }

    public void setOriPath(String oriPath) {
        this.oriPath = oriPath;
    }

    public List<String> getSamPath() {
        return samPath;
    }

    public void setSamPath(List<String> samPath) {
        this.samPath = samPath;
    }
}
