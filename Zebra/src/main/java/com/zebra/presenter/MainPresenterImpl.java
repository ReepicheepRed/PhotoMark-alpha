package com.zebra.presenter;

import android.app.Activity;
import android.content.Intent;
import android.gril.activity.AlbumsActivity;
import android.gril.bean.PhotoUpImageItem;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.sticker.app.camera.CameraManager;
import com.xiaopo.flying.photolayout.ProcessActivity;
import com.zebra.MarkBatchActivity;
import com.zebra.MarkPosterActivity;
import com.zebra.SaveCompleteActivity;
import com.zebra.bean.Banner;
import com.zebra.contract.MainContract;
import com.zebra.util.Constant;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
* Created by zhiPeng.S on 2016/11/18
*/

public class MainPresenterImpl implements MainContract.Presenter{
    private Activity activity;
    private MainContract.View mainView;

    public MainPresenterImpl(Activity activity, MainContract.View mainView) {
        this.activity = activity;
        this.mainView = mainView;
    }

    @Override
    public void obtainBannerInfo() {
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Camera/slide.ashx");
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                List<Banner> data = JSON.parseArray(result,Banner.class);
                mainView.showBanner(data);

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

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent();
        switch (position){
            case 0:
                CameraManager.getInst().openCamera(activity);
                break;
            case 1:
                intent.setClass(activity, AlbumsActivity.class);
                activity.startActivityForResult(intent,requestCode_batch);
                break;
            case 2:
                intent.setClass(activity, AlbumsActivity.class);
                activity.startActivityForResult(intent,requestCode_puzzle);
                break;
            case 3:
                intent.setClass(activity, MarkPosterActivity.class);
                activity.startActivity(intent);
                break;
        }
    }

    private final int requestCode_photo = 0, requestCode_batch = 1, requestCode_puzzle = 2, requestCode_post = 3;
    private final int resultCode_photo = 0, resultCode_batch = 1, resultCode_puzzle = 2, resultCode_post = 3;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case requestCode_photo:
                break;
            case requestCode_batch:
                switch (resultCode){
                    case resultCode_batch:
                        Bundle bundle = data.getExtras();
                        @SuppressWarnings("unchecked")
                        ArrayList<PhotoUpImageItem> selectImages =
                                (ArrayList<PhotoUpImageItem>) bundle.getSerializable("selectIma");
                        int size = selectImages == null ? 0 : selectImages.size();
                        String[] urls = new String[size];
                        for (int i = 0; i < size; i++) {
                            urls[i] = selectImages.get(i).getImagePath();
                        }
                        Intent intent = new Intent(activity, MarkBatchActivity.class);
                        intent.putExtra(MarkBatchActivity.EXTRA_IMAGE_URLS, urls);
                        intent.putExtra(MarkBatchActivity.EXTRA_IMAGE_INDEX, 0);
                        activity.startActivity(intent);
                        break;
                }
                break;
            case requestCode_puzzle:
                Intent intent = new Intent();
                switch (resultCode) {
                    case resultCode_batch:
                        Bundle bundle = data.getExtras();
                        @SuppressWarnings("unchecked")
                        ArrayList<PhotoUpImageItem> selectImages =
                                (ArrayList<PhotoUpImageItem>) bundle.getSerializable("selectIma");
                        int size = selectImages == null ? 0 : selectImages.size();
                        ArrayList<String> urls = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            urls.add(selectImages.get(i).getImagePath());
                        }
                        intent.setClass(activity,ProcessActivity.class);
                        intent.putExtra("baseUrl", Constant.getBaseUrl());
                        intent.putStringArrayListExtra("photo_path",urls);
                        activity.startActivityForResult(intent,requestCode_puzzle);
                        break;
                    case resultCode_puzzle:
                        String fileName = data.getStringExtra("picPath");
                        ArrayList<String> picPaths = new ArrayList<>();
                        picPaths.add(fileName);
                        intent.putStringArrayListExtra("picPaths",picPaths);
                        intent.setClass(activity, SaveCompleteActivity.class);
                        activity.startActivity(intent);
                        break;
                }
                break;
            case requestCode_post:
                break;
        }
    }
}