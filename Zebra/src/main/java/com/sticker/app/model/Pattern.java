package com.sticker.app.model;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/16.
 */

public class Pattern {


    /**
     * category : 1
     * items : [{"num":"1","size":"{1082,1500}","layout":[["{50,500}","{1032,500}","{1032,1450}","{50,1450}"]]},{"num":"2","size":"{1082,1500}","layout":[["{50,50}","{1032,50}","{1032,1000}","{50,1000}"]]},{"num":"3","size":"{1280,1280}","isCircle":true,"layout":[["{100,100}","{1180,100}","{1180,1180}","{100,1180}"]]},{"num":"4","size":"{1280,1280}","layout":[["{100,100}","{1180,100}","{1180,1180}","{100,1180}"]]},{"num":"5","size":"{1280,1280}","layout":[["{0,200}","{1280,200}","{1280,1080}","{0,1080}"]]},{"num":"6","size":"{1280,1280}","layout":[["{100,0}","{1180,0}","{1180,1280}","{100,1280}"]]},{"num":"7","size":"{1280,1280}","layout":[["{0,0}","{1280,0}","{1280,1280}","{0,1280}"]]}]
     */

    private String category;
    private List<ItemsBean> items;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public static class ItemsBean {
        /**
         * num : 1
         * size : {1082,1500}
         * layout : [["{50,500}","{1032,500}","{1032,1450}","{50,1450}"]]
         * isCircle : true
         */

        private String num;
        private String size;
        private boolean isCircle;
        private List<List<String>> layout;

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public boolean isIsCircle() {
            return isCircle;
        }

        public void setIsCircle(boolean isCircle) {
            this.isCircle = isCircle;
        }

        public List<List<String>> getLayout() {
            return layout;
        }

        public void setLayout(List<List<String>> layout) {
            this.layout = layout;
        }
    }
}
