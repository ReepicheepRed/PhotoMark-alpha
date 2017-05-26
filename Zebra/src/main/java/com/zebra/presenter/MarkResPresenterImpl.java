package com.zebra.presenter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.sticker.AppConstants;
import com.zebra.MarkEditActivity;
import com.zebra.R;
import com.zebra.bean.PatternsMark;
import com.zebra.bean.Template2;
import com.zebra.contract.MarkResContract;
import com.zebra.util.Constant;
import com.zebra.util.Utility;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
* Created by zhiPeng.S on 2016/11/30
*/

public class MarkResPresenterImpl implements MarkResContract.Presenter{
    private Activity activity;
    private MarkResContract.View markResView;
    private List<PatternsMark> list;
    public MarkResPresenterImpl(Activity activity, MarkResContract.View markResView) {
        this.activity = activity;
        this.markResView = markResView;
    }

    @Override
    public void obtainMaterialResource(Intent intent) {
        int fdi = intent.getIntExtra("markType",-1);
        CharSequence title = fdi == 1003 ?
                activity.getString(R.string.add_label) : activity.getString(R.string.add_mark);
        markResView.showTitle(title);
        RequestParams params  = new RequestParams(Constant.getBaseUrl() + "Camera/list.ashx");
        params.addQueryStringParameter("fdi",String.valueOf(fdi));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
//                byte[] bytes = result.getBytes();
//                FileUtil.saveFile(activity, FileUtils.getInst().getLabelMaterialPath(),"PatternsMark_Label",bytes);
                list = JSON.parseArray(result,PatternsMark.class);

                markResView.showMarkResource(list);
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
    public void onItemClick(int position) {
        PatternsMark patterns = list.get(position);
        template = new Template2();
        template.setFdi(patterns.getFdi());
        template.setCategory(patterns.getCategory());
        template.setNum(patterns.getNum());
        new AsyncTaskSave(activity).execute(patterns.getImgbig());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        activity.setResult(Activity.RESULT_OK,data);
        activity.finish();
    }

    public class AsyncTaskSave extends AsyncTask<String, Integer, String> {
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
        protected String doInBackground(String... params) {

            try{
                if(params[0] == null){
                    return null;
                }
                String filePath = Utility.convertToSdPath(mContext, params[0]);
                if(filePath == null){
                    return null;
                }
                Bitmap bitmap = Utility.getBitmapFromSdCard(activity,params[0]);
                if(bitmap == null) {
                    URL url = new URL(params[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(6000);
                    if (conn.getResponseCode() == 200) {
                        InputStream input = conn.getInputStream();
                        Bitmap bm = BitmapFactory.decodeStream(input);
                        Utility.saveImageToSd(mContext, params[0], bm);
                    }
                }
                return params[0];
            }catch(Exception e){
                System.out.println("加载网络图片错误");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            String filePath = Utility.convertToSdPath(mContext, result);
            if(filePath == null){
                return;
            }
            template.setOriPath(filePath);
            template.setContent(activity.getString(R.string.edit_tip));
            Intent intent = new Intent(activity, MarkEditActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("MarkRes",template);
            intent.putExtras(bundle);
            activity.startActivityForResult(intent,AppConstants.ACTION_EDIT_MARK);
        }
    }
}