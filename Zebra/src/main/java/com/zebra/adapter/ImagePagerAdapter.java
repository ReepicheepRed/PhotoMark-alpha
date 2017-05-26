package com.zebra.adapter;

/**
 * Created by zhiPeng.S on 2016/12/6.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.widget.RelativeLayout;

import com.common.util.ImageUtils;
import com.sticker.App;
import com.sticker.app.camera.adapter.FilterAdapter;
import com.sticker.app.camera.effect.FilterEffect;
import com.sticker.app.camera.util.GPUImageFilterTools;
import com.zebra.MarkBatchFragment;
import com.zebra.util.BitmapUtil;

import org.xutils.x;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

public class ImagePagerAdapter extends FragmentStatePagerAdapter {

    private String[] fileList;
    private List<Bitmap> bitmaps;
    private Context mContext;
    public ImagePagerAdapter(FragmentManager fm, String[] urls) {
        super(fm);
        this.fileList = urls;
        bitmaps = new ArrayList<>();
        for (String aFileList : urls) {
            ImageUtils.asyncLoadImage(x.app(), Uri.fromFile(new File(aFileList)), result ->
                    bitmaps.add(result)
            );
        }
    }

    public ImagePagerAdapter(Context context, FragmentManager fm, String[] urls) {
        this(fm,urls);
        mContext = context;
        gpuImgWidth = overlayView != null && overlayView.getWidth() > 0 ?
                overlayView.getWidth() : App.getApp().getScreenWidth();
        gpuImgHeight = overlayView != null && overlayView.getHeight() > 0 ?
                overlayView.getHeight() : BitmapUtil.dip2px(360);
    }

    @Override
    public int getCount() {
        return fileList == null ? 0 : fileList.length;
    }

    @Override
    public Fragment getItem(int position) {
        return pagerItem(position);
    }

    public Fragment pagerItem(int position){
        imageLoader(position);
        MarkBatchFragment fragment = MarkBatchFragment.newInstance(position);
        fragment.setPicUrl(fileList[position]);
        fragment.setOverlayView(overlayView);
        fragment.setmGPUImageView(mGPUImageView);
        fragment.setCurrentBitmap(currentBitmap);
        return fragment;
    }

    private int gpuImgWidth,gpuImgHeight;
    public void imageLoader(int position){
        try {
            mGPUImageView.getGPUImage().deleteImage();
            float rate = ImageUtils.getImageRadio(mContext.getContentResolver(), Uri.fromFile(new File(fileList[position])));
            boolean isMaxForHeight = ImageUtils.isMaxForHeight(fileList[position]);
            int width ,height;
            if(isMaxForHeight){
                height = gpuImgHeight;
                width = (int) (height/rate);
            }else {
                width = gpuImgWidth;
                height = (int) (width/rate);
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,height);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            mGPUImageView.setLayoutParams(params);

            final Bitmap newBitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            Canvas cv = new Canvas(newBitmap);
            RectF dst = new RectF(0, 0, width, height);
            Bitmap bitmap = bitmaps.get(position);
            cv.drawBitmap(bitmap, null, dst, null);
            currentBitmap = newBitmap;
            mGPUImageView.setImage(currentBitmap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void imageLoader(String url){
        ImageUtils.asyncLoadImage(x.app(), Uri.fromFile(new File(url)), result -> {
                    try {
                        mGPUImageView.getGPUImage().deleteImage();
                        float rate = ImageUtils.getImageRadio(mContext.getContentResolver(), Uri.fromFile(new File(url)));
                        boolean isMaxForHeight = ImageUtils.isMaxForHeight(url);
                        int width,height;
                        if(isMaxForHeight){
                            height = gpuImgHeight;
                            width = (int) (height/rate);
                        }else {
                            width = gpuImgWidth;
                            height = (int) (width/rate);
                        }
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,height);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        mGPUImageView.setLayoutParams(params);

                        final Bitmap newBitmap = Bitmap.createBitmap(width, height,
                                Bitmap.Config.ARGB_8888);
                        Canvas cv = new Canvas(newBitmap);
                        RectF dst = new RectF(0, 0, width, height);
                        cv.drawBitmap(result, null, dst, null);
                        currentBitmap = newBitmap;
                        mGPUImageView.setImage(currentBitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
        }
        );
    }

    public void onBatchAddFilter(FilterAdapter adapter, List<FilterEffect> filters, int position){
        if (adapter.getSelectFilter() != position) {
            adapter.setSelectFilter(position);
            GPUImageFilter filter =
                    GPUImageFilterTools.createFilterForType(x.app(), filters.get(position).getType());
            mGPUImageView.setFilter(filter);
            GPUImageFilterTools.FilterAdjuster mFilterAdjuster =
                    new GPUImageFilterTools.FilterAdjuster(filter);
            //可调节颜色的滤镜
            if (mFilterAdjuster.canAdjust()) {
                //mFilterAdjuster.adjust(100); 给可调节的滤镜选一个合适的值
            }
        }
    }

    private View overlayView;

    public View getOverlayView() {
        return overlayView;
    }

    public void setOverlayView(View overlayView) {
        this.overlayView = overlayView;
    }

    private Bitmap currentBitmap;

    private GPUImageView mGPUImageView;

    public Bitmap getCurrentBitmap() {
        return currentBitmap;
    }

    public void setCurrentBitmap(Bitmap currentBitmap) {
        this.currentBitmap = currentBitmap;
    }

    public GPUImageView getmGPUImageView() {
        return mGPUImageView;
    }

    public void setmGPUImageView(GPUImageView mGPUImageView) {
        this.mGPUImageView = mGPUImageView;
    }

    public List<Bitmap> getBitmaps() {
        return bitmaps;
    }

    public void setBitmaps(List<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
    }
}
