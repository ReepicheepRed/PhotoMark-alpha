package com.zebra;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.common.util.ImageUtils;
import com.common.util.StringUtils;
import com.customview.LabelView;
import com.customview.MyHighlightView;
import com.customview.MyImageViewDrawableOverlay;
import com.sticker.App;
import com.sticker.AppConstants;
import com.sticker.app.camera.util.EffectUtil;
import com.sticker.app.model.Addon;
import com.sticker.app.model.TagItem;
import com.zebra.adapter.LabelAdapter;
import com.zebra.adapter.MarkAdapter;
import com.zebra.base.BaseActivity;
import com.zebra.contract.LabelContract;
import com.zebra.contract.MarkContract;
import com.zebra.model.LabelModelImpl;
import com.zebra.presenter.LabelPresenterImpl;
import com.zebra.presenter.MarkPresenterImpl;
import com.zebra.util.DataResUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * Created by zhiPeng.S on 2016/11/28.
 */
@ContentView(R.layout.activity_mark)
public class MarkActivity extends BaseActivity implements MarkContract.View,MarkAdapter.OnItemClickListener,
        LabelContract.View,LabelAdapter.OnItemClickListener{
    private MarkContract.Presenter markPresenter;
    private LabelContract.Presenter labelPresenter;
    private final int wmarkT = 0, labelT = 1;
    private int markType;

    @ViewInject(R.id.mark_pic)
    GPUImageView mGPUImageView;

    @ViewInject(R.id.mark_src_rv)
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<LabelModelImpl> data_label;
    private List<Addon> data_wm;

    //绘图区域
    @ViewInject(R.id.mark_draw_area_rl)
    ViewGroup drawArea;
    private MyImageViewDrawableOverlay mImageView;

    //小白点标签
    private LabelView emptyLabelView;
    private List<LabelView> labels = new ArrayList<LabelView>();

    @Event({
            R.id.mark_ok_iv,
            R.id.mark_cancel_iv
    })
    private void viewClick(View view){
        switch (view.getId()){
            case R.id.mark_ok_iv:
            case R.id.mark_cancel_iv:

                Intent intent = new Intent();
                intent.putExtra("picture", picString);
                setResult(0,intent);
                finish();
                break;
        }
    }

    String picString;
    @Override
    protected void initView() {
        markType = getIntent().getIntExtra("markType",-1);
        picString = getIntent().getStringExtra("picture");
        ImageUtils.asyncLoadImage(this, Uri.fromFile(new File(picString)),
                result -> mGPUImageView.setImage(result));

        markPresenter = new MarkPresenterImpl(this,this);
        labelPresenter = new LabelPresenterImpl(this,this);

        //添加贴纸水印的画布
        View overlay = LayoutInflater.from(this).inflate( R.layout.view_drawable_overlay, null);
        mImageView = (MyImageViewDrawableOverlay) overlay.findViewById(R.id.drawable_overlay);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(App.getApp().getScreenWidth(),
                App.getApp().getScreenWidth());
        mImageView.setLayoutParams(params);
        overlay.setLayoutParams(params);
        drawArea.addView(overlay);

        //初始化空白标签
        emptyLabelView = new LabelView(this);
        emptyLabelView.setEmpty();
        EffectUtil.addLabelEditable(mImageView, drawArea, emptyLabelView,
                mImageView.getWidth() / 2, mImageView.getWidth() / 2);
        emptyLabelView.setVisibility(View.INVISIBLE);

        //初始化RecycleView
        switch (markType){
            case wmarkT:
                data_wm = new ArrayList<>();
                data_wm = EffectUtil.addonList;
                adapter = new MarkAdapter(this,data_wm);
                ((MarkAdapter)adapter).setOnItemClickListener(this);
                recyclerView.addItemDecoration(new MarkAdapter.SpacesItemDecoration(12));
                break;
            case labelT:
                data_label = new ArrayList<>();
                data_label = DataResUtil.labelList;
                adapter = new LabelAdapter(this,data_label);
                ((LabelAdapter)adapter).setOnItemClickListener(this);
                recyclerView.addItemDecoration(new LabelAdapter.SpacesItemDecoration(12));
                break;
        }

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onItemClick(int position) {
        if(position == 0) {
            Intent intent = new Intent(this,MarkResActivity.class);
            intent.putExtra("markType",markType);
            startActivity(intent);
            return;
        }
        switch (markType){
            case wmarkT:
                Addon sticker = EffectUtil.addonList.get(position-1);
                sticker.setContent("it`s good sticker");
                EffectUtil.addStickerImage(mImageView, this, sticker,
                        new EffectUtil.StickerCallback() {
                            @Override
                            public void onRemoveSticker(Addon sticker) {

                            }

                            @Override
                            public void onEditSticker(Addon sticker,MyHighlightView hv) {
                                Intent intent = new Intent(MarkActivity.this, MarkEditActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("waterMark",sticker);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                break;
            case labelT:
                String content = data_label.get(position).getContent();
                Drawable drawable = getResources().getDrawable(data_label.get(position).getId());
                TagItem tagItem = new TagItem(AppConstants.POST_TYPE_TAG,content,drawable);
                addLabel(tagItem);
                break;
        }

    }

    //添加标签
    @Override
    public void addLabel(TagItem tagItem) {
        emptyLabelView.setVisibility(View.INVISIBLE);
        if (labels.size() >= 5) {
            alert("温馨提示", "您只能添加5个标签！", "确定", null, null, null, true);
        } else {
            int left = emptyLabelView.getLeft();
            int top = emptyLabelView.getTop();
            if (labels.size() == 0 && left == 0 && top == 0) {
                left = mImageView.getWidth() / 2 - 10;
                top = mImageView.getWidth() / 2;
            }
            LabelView label = new LabelView(this);
            label.init(tagItem);
            EffectUtil.addLabelEditable(mImageView, drawArea, label, left, top);
            labels.add(label);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (AppConstants.ACTION_EDIT_LABEL== requestCode && data != null) {
            String text = data.getStringExtra(AppConstants.PARAM_EDIT_TEXT);
            if(StringUtils.isNotEmpty(text)){
                TagItem tagItem = new TagItem(AppConstants.POST_TYPE_TAG,text);
                addLabel(tagItem);
            }
        }else if(AppConstants.ACTION_EDIT_LABEL_POI== requestCode && data != null){
            String text = data.getStringExtra(AppConstants.PARAM_EDIT_TEXT);
            if(StringUtils.isNotEmpty(text)){
                TagItem tagItem = new TagItem(AppConstants.POST_TYPE_POI,text);
                addLabel(tagItem);
            }
        }
    }
}
