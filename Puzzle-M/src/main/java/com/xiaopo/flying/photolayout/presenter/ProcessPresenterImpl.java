package com.xiaopo.flying.photolayout.presenter;
import android.app.Activity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xiaopo.flying.photolayout.bean.PuzzleStyle;
import com.xiaopo.flying.photolayout.contract.ProcessContract;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
* Created by zhiPeng.S on 2017/02/28
*/

public class ProcessPresenterImpl implements ProcessContract.Presenter{
    private Activity activity;
    private ProcessContract.View processView;

    public ProcessPresenterImpl(Activity activity, ProcessContract.View processView) {
        this.activity = activity;
        this.processView = processView;
    }


    @Override
    public void obtainStyleInfo(int num, String baseUrl) {
        RequestParams params = new RequestParams(baseUrl + "Camera/FDIType.ashx");
        params.addQueryStringParameter("fdi",String.valueOf(num));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                List<PuzzleStyle> style = gson.fromJson(result,new TypeToken<List<PuzzleStyle>>(){}.getType());
                processView.showStyle(style);
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