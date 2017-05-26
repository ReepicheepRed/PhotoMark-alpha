package com.zebra.login;

import com.sticker.app.camera.CameraManager;
import com.zebra.util.Constant;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by zhiPeng.S on 2016/11/24.
 */

public class LoginManger {
    private static LoginManger mInstance;

    public static LoginManger getInst() {
        if (mInstance == null) {
            synchronized (CameraManager.class) {
                if (mInstance == null)
                    mInstance = new LoginManger();
            }
        }
        return mInstance;
    }

    public void login(String account, String password){
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
