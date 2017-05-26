package com.zebra.model;

import com.zebra.contract.MarkResContract;
import java.io.Serializable;
import java.util.List;

/**
* Created by zhiPeng.S on 2016/11/30
*/

public class MarkResModelImpl implements MarkResContract.Model,Serializable {
    /**
     * fdi : 1004
     * category : 1006
     * num : 2
     * imgbig : http://192.168.1.188//images/201611/21/1615282926.png
     * imgthum : http://192.168.1.188//images/201611/21/1614096751.png
     * imginfo : [{"midx":2,"position":"0,0,100,100","isimage":true},{"midx":2,"position":"100,100,200,200","isimage":false}]
     */

    private int fdi;
    private int category;
    private int num;
    private String imgbig;
    private String imgthum;
    private List<MarkResModelImpl.ImginfoBean> imginfo;

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

    public List<ImginfoBean> getImginfo() {
        return imginfo;
    }

    public void setImginfo(List<ImginfoBean> imginfo) {
        this.imginfo = imginfo;
    }


    public static class ImginfoBean {
        /**
         * midx : 2
         * position : 0,0,100,100
         * isimage : true
         */

        private int midx;
        private String position;
        private boolean isimage;

        public int getMidx() {
            return midx;
        }

        public void setMidx(int midx) {
            this.midx = midx;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public boolean isIsimage() {
            return isimage;
        }

        public void setIsimage(boolean isimage) {
            this.isimage = isimage;
        }
    }
}