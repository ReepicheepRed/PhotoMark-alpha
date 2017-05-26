package com.zebra;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.common.util.ImageUtils;
import com.sticker.app.camera.EffectService;
import com.sticker.app.camera.adapter.FilterAdapter;
import com.sticker.app.camera.effect.FilterEffect;
import com.sticker.app.model.Addon;
import com.zebra.adapter.ImagePagerAdapter;
import com.zebra.adapter.MarkBatchAdapter;
import com.zebra.adapter.MarkBatchFilterAdapter;
import com.zebra.base.BaseActivity;
import com.zebra.contract.MarkBatchContract;
import com.zebra.presenter.MarkBatchPresenterImpl2;
import com.zebra.util.BitmapUtil;
import com.zebra.view.HackyViewPager;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.widget.HListView;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

@ContentView(R.layout.activity_image_preview)
public class MarkBatchActivity extends BaseActivity implements MarkBatchContract.View,OnPageChangeListener,MarkBatchAdapter.OnItemClickListener{
	private static final String STATE_POSITION = "STATE_POSITION";
	public static final String EXTRA_IMAGE_INDEX = "image_index";
	public static final String EXTRA_IMAGE_URLS = "image_urls";

	private HackyViewPager mPager;
	private ImagePagerAdapter mAdapter;
	private int pagerPosition;
	@ViewInject(R.id.indicator)
	private TextView indicator;

	//用于预览的小图片
	private Bitmap smallImageBackgroud;

	@ViewInject(R.id.mBatchFl)
	private FrameLayout mBatchFl;
	private int displayHeight;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
		String[] urls = getIntent().getStringArrayExtra(EXTRA_IMAGE_URLS);

		mPager = (HackyViewPager) findViewById(R.id.pager_hv);
		mAdapter = new ImagePagerAdapter(this,getSupportFragmentManager(), urls);
		mPager.setAdapter(mAdapter);

		CharSequence text = getString(R.string.viewpager_indicator, 1, mPager
				.getAdapter().getCount());
		indicator.setText(text);
		// 更新下标
		mPager.setOnPageChangeListener(this);
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}
		mPager.setCurrentItem(pagerPosition);

		ImageUtils.asyncLoadSmallImage(this, Uri.fromFile(new File(urls[0])), result ->
				smallImageBackgroud = result);

		View view = markBatchPresenter.createMarkCanvas();
		GPUImageView view_gpu = markBatchPresenter.createMarkGpuImage();
		mAdapter.setOverlayView(view);
		mAdapter.setmGPUImageView(view_gpu);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, mPager.getCurrentItem());
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		CharSequence text = getString(R.string.viewpager_indicator,
				position + 1, mPager.getAdapter().getCount());
		indicator.setText(text);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	public ImagePagerAdapter getImagePagerAdapter() {
		return mAdapter;
	}

	public HackyViewPager getPager() {
		return mPager;
	}

	@Event({
			R.id.actionbar_back,
			R.id.actionbar_right,
			R.id.sticker_btn,
			R.id.text_btn,
			R.id.filter_btn,
			R.id.mark_cancel_iv,
			R.id.mark_ok_iv
	})
	private void viewClick(View view){
		switch (view.getId()){
			case R.id.actionbar_back:
				finish();
				break;
			case R.id.actionbar_right:
				markBatchPresenter.savePicture();
				break;
			case R.id.sticker_btn:
				initMarkToolBar();
				showSelectBottom(true,getString(R.string.watermark));
				break;
			case R.id.text_btn:
				initLabelToolBar();
				showSelectBottom(true,getString(R.string.label));
				break;
			case R.id.filter_btn:
				initFilterToolBar2();
				showSelectBottom(true,getString(R.string.filter));
				break;
			case R.id.mark_ok_iv:
			case R.id.mark_cancel_iv:
				showSelectBottom(false,"");
				break;
		}
	}

	@ViewInject(R.id.bottomTitle)
	private TextView bottomTitle;
	@ViewInject(R.id.mark_bottom_rl)
	private RelativeLayout mark_bottom_rl;
	@ViewInject(R.id.btn_area)
	private LinearLayout btn_area;

	private void showSelectBottom(boolean flag,CharSequence charSequence){
		if(flag){
			mark_bottom_rl.setVisibility(View.VISIBLE);
			btn_area.setVisibility(View.GONE);
			bottomTitle.setText(charSequence);
			if(charSequence.toString().equals(getString(R.string.filter)))
				hListView.setVisibility(View.VISIBLE);
			else
				recyclerView.setVisibility(View.VISIBLE);
			return;
		}
		mark_bottom_rl.setVisibility(View.GONE);
		btn_area.setVisibility(View.VISIBLE);
		recyclerView.setVisibility(View.GONE);
		hListView.setVisibility(View.GONE);
	}

	private MarkBatchContract.Presenter markBatchPresenter;
	@ViewInject(R.id.mBatch_rv)
	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;
	private List<Addon> data;

	@ViewInject(R.id.actionbar_right)
	private TextView actionbar_right;

	@Override
	protected void initView() {
		displayHeight = BitmapUtil.dip2px(360);
		markBatchPresenter = new MarkBatchPresenterImpl2(this,this);
		layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.addItemDecoration(new MarkBatchAdapter.SpacesItemDecoration(12));
		data = new ArrayList<>();
		adapter = new MarkBatchAdapter(this,data);
		((MarkBatchAdapter)adapter).setOnItemClickListener(this);

		actionbar_right.setText(R.string.save);

	}

	@Override
	protected void initData() {
		super.initData();

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

	}

	private void initMarkToolBar(){
		linearLayout.setVisibility(View.GONE);
		recyclerView.setVisibility(View.VISIBLE);
		recyclerView.setAdapter(adapter);
		markBatchPresenter.loadingSelfMaterial(false);
        markBatchPresenter.setMarkType(1004);
	}

	private void initLabelToolBar(){
		linearLayout.setVisibility(View.GONE);
		recyclerView.setVisibility(View.VISIBLE);
		recyclerView.setAdapter(adapter);
		markBatchPresenter.loadingSelfMaterial(true);
        markBatchPresenter.setMarkType(1003);
	}

	//初始化滤镜
	private void initFilterToolBar(){
		final List<FilterEffect> filters = EffectService.getInst().getLocalFilters();
		final MarkBatchFilterAdapter adapter = new MarkBatchFilterAdapter(this, filters,smallImageBackgroud);
		recyclerView.setAdapter(adapter);
		adapter.setOnItemClickListener(position -> {
			for (int i = 0; i < mAdapter.getCount(); i++) {
				MarkBatchFragment fragment = (MarkBatchFragment) mAdapter.instantiateItem(mPager,i);
//				fragment.onBatchAddFilter(adapter,filters,position);
			}
			mAdapter.notifyDataSetChanged();
		});
		markBatchPresenter.setMarkType(-1);
	}
	//初始化滤镜 使用HListView
	@ViewInject(R.id.toolbar_area_batch)
	private LinearLayout linearLayout;
	@ViewInject(R.id.list_tools_batch)
	private HListView hListView;
	private void initFilterToolBar2(){
		linearLayout.setVisibility(View.VISIBLE);
		recyclerView.setVisibility(View.GONE);
		Bitmap bitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.lvjing_xiaotu)).getBitmap();
		final List<FilterEffect> filters = EffectService.getInst().getLocalFilters();
		final FilterAdapter adapter = new FilterAdapter(this, filters,bitmap);
		hListView.setAdapter(adapter);
		hListView.setOnItemClickListener((parent, view, position, id) -> {
			mAdapter.onBatchAddFilter(adapter, filters, position);
            mAdapter.notifyDataSetChanged();
        });
		markBatchPresenter.setMarkType(-1);
	}

	@Override
	public void onItemClick(int position) {
		markBatchPresenter.onItemClick(position);
	}

	@Override
	public List<Addon> getData() {
		return data;
	}

	public void setData(List<Addon> data) {
		this.data = data;
	}

	@Override
	public RecyclerView.Adapter getAdapter() {
		return adapter;
	}

	@Override
	public int getDisplayHeight() {
		return displayHeight;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		markBatchPresenter.onActivityResult(requestCode, resultCode, data);
	}

}