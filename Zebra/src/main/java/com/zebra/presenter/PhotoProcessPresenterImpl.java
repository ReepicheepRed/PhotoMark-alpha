package com.zebra.presenter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.common.util.ImageUtils;
import com.common.util.StringUtils;
import com.common.util.TimeUtils;
import com.customview.MyImageViewDrawableOverlay;
import com.sticker.AppConstants;
import com.sticker.app.camera.adapter.StickerToolAdapter;
import com.sticker.app.camera.util.EffectUtil;
import com.sticker.app.model.Addon;
import com.sticker.base.BaseActivity;
import com.xiaopo.flying.photolayout.FileUtil;
import com.zebra.R;
import com.zebra.SaveCompleteActivity;
import com.zebra.bean.Template2;
import com.zebra.contract.PhotoProcessContract;
import com.zebra.db.DB_Config;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
* Created by zhiPeng.S on 2016/12/07
*/

public class PhotoProcessPresenterImpl implements PhotoProcessContract.Presenter{
    private BaseActivity activity;
    private PhotoProcessContract.View photoProcessView;
    private DbManager db;
    public PhotoProcessPresenterImpl(BaseActivity activity, PhotoProcessContract.View photoProcessView) {
        this.activity = activity;
        this.photoProcessView = photoProcessView;
        db = x.getDb(DB_Config.getDaoConfig());
    }

    @Override
    public boolean setCurrentBtn(TextView currentBtn,TextView btn) {
        if (currentBtn == null) {
            currentBtn = btn;
        } else if (currentBtn.equals(btn)) {
            return false;
        } else {
            Drawable[] drawables = currentBtn.getCompoundDrawables();
            currentBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawables[1], null, null);
        }
        Drawable myImage = x.app().getResources().getDrawable(R.drawable.select_icon);
        Drawable stickerImage = x.app().getResources().getDrawable(R.drawable.tab_icon_shuiying);
        Drawable filterImage = x.app().getResources().getDrawable(R.drawable.tab_icon_lvjing);
        Drawable labelImage = x.app().getResources().getDrawable(R.drawable.tab_icon_biaoqian);
        switch (currentBtn.getId()){
            case R.id.sticker_btn:
                btn.setCompoundDrawablesWithIntrinsicBounds(null, stickerImage, null, null);
                break;
            case R.id.filter_btn:
                btn.setCompoundDrawablesWithIntrinsicBounds(null, filterImage, null, null);
                break;
            case R.id.text_btn:
                btn.setCompoundDrawablesWithIntrinsicBounds(null, labelImage, null, null);
                break;
        }

        currentBtn = btn;
        return true;
    }

    //保存图片
    public void savePicture(MyImageViewDrawableOverlay mImageView, GPUImageView mGPUImageView, Bitmap currentBitmap){
        //加滤镜
        final Bitmap newBitmap = Bitmap.createBitmap(mImageView.getWidth(), mImageView.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newBitmap);
        RectF dst = new RectF(0, 0, mImageView.getWidth(), mImageView.getHeight());
        try {
            cv.drawBitmap(mGPUImageView.capture(), null, dst, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
            cv.drawBitmap(currentBitmap, null, dst, null);
        }
        //加贴纸水印
        EffectUtil.applyOnSave(cv, mImageView);

        new SavePicToLocalAlbum().execute(newBitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case AppConstants.ACTION_EDIT_MARK:
                loadingSelfMaterial(false);
                break;
            case AppConstants.ACTION_EDIT_LABEL:
                loadingSelfMaterial(true);
                break;
        }
        if(data != null)
            addMark(data);
    }

    private void addMark(Intent data){
        List<Addon> list = photoProcessView.getData();
        Template2 template = (Template2) data.getSerializableExtra("newMark");
        String imgPath = template.getImgPath();
        int position =0;
        for (Addon addon: list) {
            String aImgPath = addon.getImgPath();
            if(aImgPath.equals(imgPath))
                position = list.indexOf(addon);
        }
        photoProcessView.addMark(++position);
    }

    @Override
    public void loadingSelfMaterial(boolean isLabel) {
        List<Addon> list = photoProcessView.getData();
        list.clear();
        List<Template2> data = null;
        int label = 1003,mark = 1004;
        try {
            data = db.selector(Template2.class).where("fdi", "=", isLabel ? label : mark).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        int size = data == null ? 0 : data.size();
        for (int i = 0; i < size; i++) {
            Template2 template = data.get(i);
            Addon addon = new Addon();
            addon.setId(template.getId());
            addon.setFdi(template.getFdi());
            addon.setCategory(template.getCategory());
            addon.setNum(template.getNum());
            addon.setImgPath(template.getImgPath());
            addon.setOriPath(template.getOriPath());
            addon.setContent(template.getContent());
            list.add(addon);
        }



//        HashMap<String, String> fileList = new LinkedHashMap<>();
//        File file = new File(FileUtils.getInst().getLabelMaterialPath(FileUtils.MarkSelf));
//        FileUtil.getFileList(file,fileList);
//        Iterator iterator = fileList.entrySet().iterator();
//        while (iterator.hasNext()){
//            Map.Entry entry = (Map.Entry)iterator.next();
//            String fileName = (String) entry.getKey();
//            String filePath = (String) entry.getValue();
//
//            Uri uri = FileUtil.getImageContentUri(activity,new File(filePath));
//            if(uri == null) break;
//            Addon addon = new Addon();
//            addon.setImgbig(uri.toString());
//            addon.setPath(filePath);
//            list.add(addon);
//        }
        Collections.reverse(list);
        StickerToolAdapter adapter = photoProcessView.getAdapter();
        adapter.setData(list);
        adapter.notifyDataSetChanged();

    }


    private class SavePicToLocalAlbum extends AsyncTask<Bitmap,Void,String> {
        Bitmap bitmap;
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
//                fileName = ImageUtils.saveToFile(FileUtils.getInst().getSystemPhotoPath() + "/"+ picName + ".jpg", false, bitmap);

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
}