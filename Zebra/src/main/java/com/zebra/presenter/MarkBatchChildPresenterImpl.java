package com.zebra.presenter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.customview.MyImageViewDrawableOverlay;
import com.sticker.app.camera.util.EffectUtil;
import com.zebra.base.BaseActivity;
import com.zebra.contract.MarkBatchChildContract;
import com.zebra.util.SavePicture;

import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
* Created by zhiPeng.S on 2016/12/09
*/

public class MarkBatchChildPresenterImpl implements MarkBatchChildContract.Presenter{
    private BaseActivity activity;
    private MarkBatchChildContract.View markBatchChildView;

    public MarkBatchChildPresenterImpl(BaseActivity activity, MarkBatchChildContract.View markBatchChildView) {
        this.activity = activity;
        this.markBatchChildView = markBatchChildView;
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

        new SavePicture(activity).execute(newBitmap);

    }

}