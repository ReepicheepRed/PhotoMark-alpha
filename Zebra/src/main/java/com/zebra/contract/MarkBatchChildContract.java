package com.zebra.contract;

import android.graphics.Bitmap;

import com.customview.MyImageViewDrawableOverlay;

import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * Created by zhiPeng.S on 2016/12/6.
 */

public class MarkBatchChildContract {

public interface View{
}

public interface Presenter{
    void savePicture(MyImageViewDrawableOverlay mImageView, GPUImageView mGPUImageView, Bitmap currentBitmap);
}

public interface Model{
}


}