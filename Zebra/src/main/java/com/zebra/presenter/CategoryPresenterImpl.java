package com.zebra.presenter;
import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zebra.bean.Category;
import com.zebra.contract.CategoryContract;
import com.zebra.util.Constant;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
* Created by zhiPeng.S on 2016/12/12
*/

public class CategoryPresenterImpl implements CategoryContract.Presenter{
    private Activity activity;
    private CategoryContract.View categoryView;

    public CategoryPresenterImpl(Activity activity, CategoryContract.View categoryView) {
        this.activity = activity;
        this.categoryView = categoryView;
    }

    @Override
    public void obtainCategory() {
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Camera/FDIList.ashx");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                List<Category> category = JSON.parseObject(result, new TypeReference<List<Category>>(){});
                categoryView.showCategory(category);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}