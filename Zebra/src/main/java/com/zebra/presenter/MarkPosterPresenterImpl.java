package com.zebra.presenter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.zebra.R;
import com.zebra.bean.PatternsMark;
import com.zebra.bean.Template2;
import com.zebra.contract.MarkPosterContract;
import com.zebra.util.Constant;
import com.zebra.util.Utility;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import postermaster.MainActivity;

/**
* Created by zhiPeng.S on 2016/12/15
*/

public class MarkPosterPresenterImpl implements MarkPosterContract.Presenter{
    private Activity activity;
    private MarkPosterContract.View markPosterView;
    private List<PatternsMark> list;
    public MarkPosterPresenterImpl(Activity activity, MarkPosterContract.View markPosterView) {
        this.activity = activity;
        this.markPosterView = markPosterView;
    }

    @Override
    public void obtainPosterResource() {
        RequestParams params  = new RequestParams(Constant.getBaseUrl() + "Camera/list.ashx");
        params.addQueryStringParameter("fdi","1005");
//        params.addQueryStringParameter("category","1025");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
//                byte[] bytes = result.getBytes();
//                FileUtil.saveFile(activity, FileUtils.getInst().getLabelMaterialPath(),"Template_Label",bytes);
                list = JSON.parseArray(result,PatternsMark.class);
                markPosterView.showPoster(list);

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

    Template2 template;

    @Override
    public void onItemClick(int position, View view) {
        PatternsMark patterns = list.get(position);
        template = new Template2();
        template.setFdi(patterns.getFdi());
        template.setCategory(patterns.getCategory());
        template.setNum(patterns.getNum());
        List<PatternsMark.ImginfoBean> images = patterns.getImginfo();
        int size = images.size() + 1;
        String[] url = new String[size];
        for (int i = 0; i < size ; i++) {
            if(i == 0) {
                url[i] = patterns.getImgbig();
                continue;
            }
            url[i] = images.get(i-1).getImg();
        }
        new AsyncTaskSave(activity).execute(url);


    }

    public class AsyncTaskSave extends AsyncTask<String, Integer, String[]> {
        private Context mContext;
        private ProgressDialog progressDialog;
        public AsyncTaskSave(Context context){
            mContext = context;
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading...");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String[] doInBackground(String... params) {
            for (int i = 0; i < params.length; i++) {
                try{
                    if(params[i] == null){
                        return null;
                    }
                    String filePath = Utility.convertToSdPath(mContext, params[i]);
                    if(filePath == null){
                        return null;
                    }
                    Bitmap bitmap = Utility.getBitmapFromSdCard(activity,params[i]);
                    if(bitmap == null) {
                        URL url = new URL(params[i]);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(6000);
                        if (conn.getResponseCode() == 200) {
                            InputStream input = conn.getInputStream();
                            Bitmap bm = BitmapFactory.decodeStream(input);
                            Utility.saveImageToSd(mContext, params[i], bm);
                        }
                    }
                }catch(Exception e){
                    System.out.println("加载网络图片错误");
                    e.printStackTrace();
                }
            }
            return params;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            List<String> path = new ArrayList<>();
            for (int i = 0; i < result.length; i++) {
                String filePath = Utility.convertToSdPath(mContext, result[i]);
                if(filePath != null){
                    path.add(filePath);
                }
            }

            if(path.size() < result.length) return;

            template.setOriPath(path.get(0));
            path.remove(0);
            template.setSamPath(path);
            template.setContent(activity.getString(R.string.edit_tip));

            ArrayList<PatternsMark> arrayList = new ArrayList<>();
            for (int i = 0; i <list.size() ; i++) {
                PatternsMark patternMark  = list.get(i);
                if(patternMark.getCategory() == template.getCategory()){
                    arrayList.add(patternMark);
                }
            }

            Intent intent = new Intent(activity, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("poster",template);
            bundle.putSerializable("patterns", arrayList);
            intent.putExtras(bundle);
            activity.startActivity(intent);
        }

    }

}