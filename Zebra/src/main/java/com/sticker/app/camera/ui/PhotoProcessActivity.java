package com.sticker.app.camera.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.common.util.ImageUtils;
import com.customview.MyHighlightView;
import com.customview.MyImageViewDrawableOverlay;
import com.sticker.AppConstants;
import com.zebra.MarkEditActivity;
import com.zebra.MarkResActivity;
import com.zebra.R;
import com.sticker.App;
import com.sticker.app.camera.CameraBaseActivity;
import com.sticker.app.camera.EffectService;
import com.sticker.app.camera.adapter.FilterAdapter;
import com.sticker.app.camera.adapter.StickerToolAdapter;
import com.sticker.app.camera.effect.FilterEffect;
import com.sticker.app.camera.util.EffectUtil;
import com.sticker.app.camera.util.GPUImageFilterTools;
import com.sticker.app.model.Addon;
import com.zebra.contract.PhotoProcessContract;
import com.zebra.presenter.PhotoProcessPresenterImpl;
import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;
import it.sephiroth.android.library.widget.HListView;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * 图片处理界面
 * Created by sky on 2015/7/8.
 * Weibo: http://weibo.com/2030683111
 * Email: 1132234509@qq.com
 */
public class PhotoProcessActivity extends CameraBaseActivity implements View.OnClickListener,
        PhotoProcessContract.View,EffectUtil.StickerCallback,it.sephiroth.android.library.widget.AdapterView.OnItemClickListener {

    //滤镜图片
    @InjectView(R.id.gpuimage)
    GPUImageView mGPUImageView;
    //绘图区域
    @InjectView(R.id.drawing_view_container)
    ViewGroup drawArea;

    //底部按钮
    @InjectView(R.id.sticker_btn)
    TextView stickerBtn;
    @InjectView(R.id.filter_btn)
    TextView filterBtn;
    @InjectView(R.id.text_btn)
    TextView labelBtn;

    //工具区
    @InjectView(R.id.list_tools)
    HListView bottomToolBar;
    @InjectView(R.id.toolbar_area)
    ViewGroup toolArea;
    private MyImageViewDrawableOverlay mImageView;

    //当前选择底部按钮
    private TextView currentBtn;
    //当前图片
    private Bitmap currentBitmap;
    //用于预览的小图片
    private Bitmap smallImageBackgroud;

    //底部选择区
    @InjectView(R.id.bottomTitle)
    TextView bottomTitle;
    @InjectView(R.id.mark_cancel_iv)
    ImageView bottomCancel;
    @InjectView(R.id.mark_ok_iv)
    ImageView bottomConfirm;
    @InjectView(R.id.mark_bottom_rl)
    RelativeLayout mark_bottom_rl;
    @InjectView(R.id.btn_area)
    LinearLayout btn_area;

    private PhotoProcessContract.Presenter photoProcessPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_process);
        ButterKnife.inject(this);
        EffectUtil.clear();
        initView();
        initEvent();
//        initStickerToolBar();

        ImageUtils.asyncLoadImage(this, getIntent().getData(), result -> {
            final Bitmap newBitmap = Bitmap.createBitmap(mImageView.getWidth(), mImageView.getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas cv = new Canvas(newBitmap);
            RectF dst = new RectF(0, 0, mImageView.getWidth(), mImageView.getHeight());
            cv.drawBitmap(result, null, dst, null);
            currentBitmap = newBitmap;
            mGPUImageView.setImage(currentBitmap);
        });


        ImageUtils.asyncLoadSmallImage(this, getIntent().getData(), result ->
                smallImageBackgroud = result);


    }
    private void initView() {
        photoProcessPresenter = new PhotoProcessPresenterImpl(this,this);
        data = new ArrayList<>();
        adapter = new StickerToolAdapter(PhotoProcessActivity.this, data);

        //添加贴纸水印的画布
        View overlay = LayoutInflater.from(PhotoProcessActivity.this).inflate(
                R.layout.view_drawable_overlay, null);
        mImageView = (MyImageViewDrawableOverlay) overlay.findViewById(R.id.drawable_overlay);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(App.getApp().getScreenWidth(),
                App.getApp().getScreenWidth());
        mImageView.setLayoutParams(params);
        overlay.setLayoutParams(params);
        drawArea.addView(overlay);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sticker_btn:
                initStickerToolBar();
                showSelectBottom(true,getString(R.string.watermark));
                break;
            case R.id.text_btn:
                initLabelToolBar();
                showSelectBottom(true,getString(R.string.label));
                break;
            case R.id.filter_btn:
                initFilterToolBar();
                showSelectBottom(true,getString(R.string.filter));
                break;
            case R.id.mark_ok_iv:
            case R.id.mark_cancel_iv:
                showSelectBottom(false,"");
                break;
        }
    }

    private void initEvent() {
        stickerBtn.setOnClickListener(this);
        filterBtn.setOnClickListener(this);
        labelBtn.setOnClickListener(this);
        bottomConfirm.setOnClickListener(this);
        bottomCancel.setOnClickListener(this);

        titleBar.setRightBtnOnclickListener(v ->
                photoProcessPresenter.savePicture(mImageView,mGPUImageView,currentBitmap));
    }

    private void showSelectBottom(boolean flag,CharSequence charSequence){
        if(flag){
            mark_bottom_rl.setVisibility(View.VISIBLE);
            btn_area.setVisibility(View.GONE);
            bottomToolBar.setVisibility(View.VISIBLE);
            bottomTitle.setText(charSequence);
            return;
        }
        mark_bottom_rl.setVisibility(View.GONE);
        btn_area.setVisibility(View.VISIBLE);
        bottomToolBar.setVisibility(View.GONE);
    }

    private List<Addon> data;
    private StickerToolAdapter adapter;
    //初始化贴图
    private void initStickerToolBar(){
        markType = mark;

        bottomToolBar.setAdapter(adapter);
        bottomToolBar.setOnItemClickListener(this);
        photoProcessPresenter.setCurrentBtn(currentBtn,stickerBtn);
        photoProcessPresenter.loadingSelfMaterial(false);
    }
    private void initLabelToolBar(){
        markType = label;

        bottomToolBar.setAdapter(adapter);
        bottomToolBar.setOnItemClickListener(this);
        photoProcessPresenter.setCurrentBtn(currentBtn,labelBtn);
        photoProcessPresenter.loadingSelfMaterial(true);
    }

    private int markType;
    private final int label = 1003,mark = 1004;
    @Override
    public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> parent,
                            View view, int position, long id) {
        if(position == 0) {
            Intent intent = new Intent(this,MarkResActivity.class);
            intent.putExtra("markType",markType);
            switch (markType){
                case label:
                    startActivityForResult(intent,AppConstants.ACTION_EDIT_LABEL);
                    break;
                case mark:
                    startActivityForResult(intent,AppConstants.ACTION_EDIT_MARK);
                    break;
            }

            return;
        }
        addMark(position);
    }

    @Override
    public void addMark(int position) {
        if(data == null || data.size() < 1) return;
        Addon sticker = data.get(position-1);
        EffectUtil.addStickerImage(mImageView, PhotoProcessActivity.this,
                sticker, PhotoProcessActivity.this);
    }

    @Override
    public List<Addon> getData() {
        return data;
    }

    @Override
    public void setData(List<Addon> data) {
        this.data = data;
    }

    @Override
    public StickerToolAdapter getAdapter() {
        return adapter;
    }

    //初始化滤镜
    private void initFilterToolBar(){
        Bitmap bitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.lvjing_xiaotu)).getBitmap();
        final List<FilterEffect> filters = EffectService.getInst().getLocalFilters();
        final FilterAdapter adapter = new FilterAdapter(PhotoProcessActivity.this, filters,bitmap);
        bottomToolBar.setAdapter(adapter);
        bottomToolBar.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {

            if (adapter.getSelectFilter() != arg2) {
                adapter.setSelectFilter(arg2);
                GPUImageFilter filter = GPUImageFilterTools.createFilterForType(
                        PhotoProcessActivity.this, filters.get(arg2).getType());
                mGPUImageView.setFilter(filter);
                GPUImageFilterTools.FilterAdjuster mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(filter);
                //可调节颜色的滤镜
                if (mFilterAdjuster.canAdjust()) {
                    //mFilterAdjuster.adjust(100); 给可调节的滤镜选一个合适的值
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        photoProcessPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRemoveSticker(Addon sticker) {

    }

    @Override
    public void onEditSticker(Addon sticker,MyHighlightView hv) {
        Intent intent = new Intent(PhotoProcessActivity.this, MarkEditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("MarkRes",sticker);
        intent.putExtras(bundle);
        EffectUtil.removeStickerImage(mImageView,hv);
        switch (sticker.getFdi()){
            case label:
                startActivityForResult(intent, AppConstants.ACTION_EDIT_LABEL);
                break;
            case mark:
                startActivityForResult(intent, AppConstants.ACTION_EDIT_MARK);
                break;

        }

    }
}
