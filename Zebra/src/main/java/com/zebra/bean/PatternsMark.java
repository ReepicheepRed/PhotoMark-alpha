package com.zebra.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/12/22.
 */

public class PatternsMark implements Serializable {

    /**
     * fdi : 1005
     * category : 1008
     * num : 3
     * title : 1005_1008_3
     * imgbig : http://192.168.1.188//images/201612/23/1419480534.png
     * imgthum : http://192.168.1.188//images/201612/23/1419480514.png
     * thumwidth : 750
     * thumheight : 1334
     * imginfo : [{"img":"http://192.168.1.188//images/201612/23/1419595791.png","sort":1},{"img":"http://192.168.1.188//images/201612/23/1419597661.png","sort":2},{"img":"http://192.168.1.188//images/201612/23/1420000521.png","sort":3}]
     */

    private int fdi;
    private int category;
    private int num;
    private String title;
    private String imgbig;
    private String imgthum;
    private int thumwidth;
    private int thumheight;
    private List<ImginfoBean> imginfo;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgbig() {
        return imgbig;
    }

    public void setImgbig(String imgbig) {
        this.imgbig = imgbig;
    }

    public String getImgthum() {
        return imgthum;
    }

    public void setImgthum(String imgthum) {
        this.imgthum = imgthum;
    }

    public int getThumwidth() {
        return thumwidth;
    }

    public void setThumwidth(int thumwidth) {
        this.thumwidth = thumwidth;
    }

    public int getThumheight() {
        return thumheight;
    }

    public void setThumheight(int thumheight) {
        this.thumheight = thumheight;
    }

    public List<ImginfoBean> getImginfo() {
        return imginfo;
    }

    public void setImginfo(List<ImginfoBean> imginfo) {
        this.imginfo = imginfo;
    }

    public static class ImginfoBean implements Serializable{
        /**
         * img : http://192.168.1.188//images/201612/23/1419595791.png
         * sort : 1
         */

        private String img;
        private int sort;

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }
    }
}
