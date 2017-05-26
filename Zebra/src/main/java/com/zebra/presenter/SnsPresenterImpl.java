package com.zebra.presenter;
import android.app.Activity;

import com.zebra.contract.SnsContract;
import com.zebra.util.Constant;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
* Created by zhiPeng.S on 2016/12/13
*/

public class SnsPresenterImpl implements SnsContract.Presenter{
    private Activity activity;
    private SnsContract.View snsView;

    public SnsPresenterImpl(Activity activity, SnsContract.View snsView) {
        this.activity = activity;
        this.snsView = snsView;
    }

    @Override
    public void obtainSnsInfo() {
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

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