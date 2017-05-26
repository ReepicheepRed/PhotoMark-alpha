package com.zebra.contract;

import com.zebra.bean.Category;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/12/12.
 */

public class CategoryContract {
    
public interface View{
    void showCategory(List<Category> data);
}

public interface Presenter{
    void obtainCategory();
}

public interface Model{
}


}