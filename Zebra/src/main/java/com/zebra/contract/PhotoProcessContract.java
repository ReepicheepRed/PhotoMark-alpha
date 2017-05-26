package com.zebra.contract;

import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.TextView;

import com.customview.MyImageViewDrawableOverlay;
import com.sticker.app.camera.adapter.StickerToolAdapter;
import com.sticker.app.model.Addon;

import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * Created by zhiPeng.S on 2016/12/7.
 */

public class PhotoProcessContract {
    
public interface View{
    void addMark(int position);
    List<Addon> getData();
    void setData(List<Addon> data);
    StickerToolAdapter getAdapter();
}

public interface Presenter{
    boolean setCurrentBtn(TextView currentBtn, TextView btn);
    void savePicture(MyImageViewDrawableOverlay mImageView, GPUImageView mGPUImageView, Bitmap currentBitmap);
    void onActivityResult(int requestCode, int resultCode, Intent data);
    void loadingSelfMaterial(boolean isLabel);
}

public interface Model{
}


}