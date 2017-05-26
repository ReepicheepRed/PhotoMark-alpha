package com.zebra.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.xiaopo.flying.photolayout.FileUtil;
import com.common.util.ImageUtils;
import com.common.util.StringUtils;
import com.common.util.TimeUtils;
import com.zebra.SaveCompleteActivity;
import com.zebra.base.BaseActivity;

import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by zhiPeng.S on 2016/12/22.
 */

public class SavePicture extends AsyncTask<Bitmap,Void,String> {
    Bitmap bitmap;
    private BaseActivity activity;
    public SavePicture(BaseActivity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.showProgressDialog("Processing...");
    }

    @Override
    protected String doInBackground(Bitmap... params) {
        String fileName = null;
        try {
            bitmap = params[0];

            String picName = TimeUtils.dtFormat(new Date(), "yyyyMMdd_HHmmss");
            fileName = ImageUtils.saveToFile(FileUtil.getFolderName("PhotoMark") + "/"+ picName + ".jpg", false, bitmap);

        } catch (Exception e) {
            e.printStackTrace();
            activity.toast("图片处理错误，请退出相机并重试", Toast.LENGTH_LONG);
        }
        return fileName;
    }

    @Override
    protected void onPostExecute(String fileName) {
        super.onPostExecute(fileName);
        activity.dismissProgressDialog();
        if (StringUtils.isEmpty(fileName)) {
            return;
        }

        Intent intent_u = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(fileName));
        intent_u.setData(uri);
        x.app().sendBroadcast(intent_u);

        Intent intent = new Intent(activity, SaveCompleteActivity.class);
        ArrayList<String> picPaths = new ArrayList<>();
        picPaths.add(fileName);
        intent.putStringArrayListExtra("picPaths",picPaths);
        activity.startActivity(intent);
    }
}
