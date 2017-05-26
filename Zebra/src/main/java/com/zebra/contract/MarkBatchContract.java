package com.zebra.contract;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import com.sticker.app.model.Addon;

import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * Created by zhiPeng.S on 2016/12/6.
 */

public class MarkBatchContract {
    
public interface View{
    List<Addon> getData();
    RecyclerView.Adapter getAdapter();
    int getDisplayHeight();
}

public interface Presenter{
    void setMarkType(int type);
    void onItemClick(int position);
    void loadingSelfMaterial(boolean isLabel);
    void savePicture();
    void onActivityResult(int requestCode, int resultCode, Intent data);
    android.view.View createMarkCanvas();
    GPUImageView createMarkGpuImage();
}

public interface Model{
}


}