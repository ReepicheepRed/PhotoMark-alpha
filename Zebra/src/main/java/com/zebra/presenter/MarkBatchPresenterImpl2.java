package com.zebra.presenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.common.util.ImageUtils;
import com.common.util.TimeUtils;
import com.customview.MyHighlightView;
import com.customview.MyImageViewDrawableOverlay;
import com.sticker.App;
import com.sticker.AppConstants;
import com.sticker.app.camera.util.EffectUtil;
import com.sticker.app.model.Addon;
import com.xiaopo.flying.photolayout.FileUtil;
import com.zebra.MarkBatchActivity;
import com.zebra.MarkEditActivity;
import com.zebra.MarkResActivity;
import com.zebra.R;
import com.zebra.SaveCompleteActivity;
import com.zebra.adapter.ImagePagerAdapter;
import com.zebra.adapter.MarkBatchAdapter;
import com.zebra.base.BaseActivity;
import com.zebra.bean.Template2;
import com.zebra.contract.MarkBatchContract;
import com.zebra.db.DB_Config;
import com.zebra.view.HackyViewPager;

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
* Created by zhiPeng.S on 2016/12/06
*/

public class MarkBatchPresenterImpl2 implements MarkBatchContract.Presenter,EffectUtil.StickerCallback{
    private Activity activity;
    private MarkBatchContract.View markBatchView;
    private DbManager db;
    private int markType;
    private final int label = 1003, mark = 1004;
    private GPUImageView mBatchgpuImage;

    public MarkBatchPresenterImpl2(Activity activity, MarkBatchContract.View markBatchView) {
        this.activity = activity;
        this.markBatchView = markBatchView;
        db = x.getDb(DB_Config.getDaoConfig());
        params = new ViewGroup.LayoutParams(App.getApp().getScreenWidth(),markBatchView.getDisplayHeight());
        progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setMessage("Processing...");
    }

    @Override
    public void setMarkType(int type) {
        markType = type;
    }


    @Override
    public void onItemClick(int position) {
        if(position == 0){
            Intent intent = new Intent(activity, MarkResActivity.class);
            intent.putExtra("markType",markType);
            switch (markType){
                case label:
                    ((MarkBatchActivity)activity).startActivityForResult(intent, AppConstants.ACTION_EDIT_LABEL);
                    break;
                case mark:
                    ((MarkBatchActivity)activity).startActivityForResult(intent, AppConstants.ACTION_EDIT_MARK);
                    break;
            }
            return;
        }
        List<Addon> data = ((MarkBatchActivity)activity).getData();
        ImagePagerAdapter mAdapter = ((MarkBatchActivity)activity).getImagePagerAdapter();
        HackyViewPager mPager = ((MarkBatchActivity) activity).getPager();
        onBatchAddMark(data, position - 1);
        mAdapter.notifyDataSetChanged();

    }

    public void onBatchAddMark(List<Addon> data, int position){
        Addon sticker = data.get(position);
        EffectUtil.addStickerImage(mImageView, x.app(),sticker, this);
    }

    private void addMark(Intent data){
        List<Addon> list = markBatchView.getData();
        Template2 template = (Template2) data.getSerializableExtra("newMark");
        String imgPath = template.getImgPath();
        int position =0;
        for (Addon addon: list) {
            String aImgPath = addon.getImgPath();
            if(aImgPath.equals(imgPath))
                position = list.indexOf(addon);
        }
        onItemClick(++position);
    }

    @Override
    public void loadingSelfMaterial(boolean isLabel) {
        List<Addon> list = markBatchView.getData();
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

        Collections.reverse(list);
        MarkBatchAdapter adapter = (MarkBatchAdapter) markBatchView.getAdapter();
        adapter.setData(list);
        adapter.notifyDataSetChanged();

    }

    private ImagePagerAdapter mAdapter;
    private int pagerPosition = 0;
    private ProgressDialog progressDialog;

    @Override
    public void savePicture() {
        mAdapter = ((MarkBatchActivity)activity).getImagePagerAdapter();
        progressDialog.setMax(mAdapter.getCount());
        progressDialog.setProgress(pagerPosition);
        progressDialog.show();


        mAdapter.imageLoader(pagerPosition);
        GPUImageView mGPUImageView = mAdapter.getmGPUImageView();
        Bitmap currentBitmap = mAdapter.getCurrentBitmap();
        savePicture(mImageView,mGPUImageView,currentBitmap);


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


    private ViewGroup.LayoutParams params;
    private MyImageViewDrawableOverlay mImageView;
    public View createMarkCanvas(){
        View overlay = LayoutInflater.from(x.app()).inflate(R.layout.view_drawable_overlay, null);
        mImageView = (MyImageViewDrawableOverlay) overlay.findViewById(R.id.drawable_overlay);

        mImageView.setLayoutParams(params);
        overlay.setLayoutParams(params);
        return overlay;
    }

    public GPUImageView createMarkGpuImage(){
        View view = LayoutInflater.from(x.app()).inflate(R.layout.view_gpuimageview, null);
        GPUImageView  mGPUImageView = (GPUImageView) view.findViewById(R.id.mBatch_gpuImage);
        return mGPUImageView;
    }

    public void savePicture(MyImageViewDrawableOverlay mImageView, GPUImageView mGPUImageView, Bitmap currentBitmap) {
        //加滤镜
        final Bitmap newBitmap = Bitmap.createBitmap(mGPUImageView.getWidth(), mGPUImageView.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newBitmap);
        RectF dst = new RectF(0, 0, mGPUImageView.getWidth(), mGPUImageView.getHeight());
        try {
            cv.drawBitmap(mGPUImageView.capture(), null, dst, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
            cv.drawBitmap(currentBitmap, null, dst, null);
        }
        //加贴纸水印
        EffectUtil.applyOnSave(cv, mImageView);

        handler.post(() -> {
            String fileName = null;
            try {
                String picName = TimeUtils.dtFormat(new Date(), "yyyyMMddHHmmss");
                fileName = ImageUtils.saveToFile(FileUtil.getFolderName("PhotoMark") + "/"+ picName + ".jpg", false, newBitmap);
            } catch (Exception e) {
                e.printStackTrace();
                ((BaseActivity)activity).toast("图片处理错误，请退出相机并重试", Toast.LENGTH_LONG);
            }
            Message msg = handler.obtainMessage();
            msg.obj = fileName;
            handler.sendMessage(msg);
        });

    }

    private ArrayList<String> picPaths = new ArrayList<>();

    @SuppressWarnings("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pagerPosition++;
            progressDialog.setProgress(pagerPosition);

            String fileName = (String) msg.obj;
            picPaths.add(fileName);
            activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(new File(fileName))));

            if(pagerPosition == mAdapter.getCount()){
                progressDialog.dismiss();
                Intent intent = new Intent(activity, SaveCompleteActivity.class);
                intent.putStringArrayListExtra("picPaths", picPaths);
                activity.startActivity(intent);
                activity.finish();
                return;
            }

            mAdapter.imageLoader(pagerPosition);
            GPUImageView mGPUImageView = mAdapter.getmGPUImageView();
            Bitmap currentBitmap = mAdapter.getCurrentBitmap();
            savePicture(mImageView,mGPUImageView,currentBitmap);
        }
    };


    @Override
    public void onRemoveSticker(Addon sticker) {

    }

    @Override
    public void onEditSticker(Addon sticker,MyHighlightView hv) {
        Intent intent = new Intent(activity, MarkEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("MarkRes",sticker);
        intent.putExtras(bundle);
        EffectUtil.removeStickerImage(mImageView,hv);
        switch (sticker.getFdi()){
            case 1003:
                activity.startActivityForResult(intent, AppConstants.ACTION_EDIT_LABEL);
                break;
            case 1004:
                activity.startActivityForResult(intent, AppConstants.ACTION_EDIT_MARK);
                break;
        }
    }
}