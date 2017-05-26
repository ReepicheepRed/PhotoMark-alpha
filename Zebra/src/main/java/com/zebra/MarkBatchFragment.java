package com.zebra;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.zebra.adapter.ImagePagerAdapter;
import com.zebra.base.BaseActivity;
import com.zebra.contract.MarkBatchChildContract;
import com.zebra.presenter.MarkBatchChildPresenterImpl;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageView;


@ContentView(R.layout.fragment_image_preview)
public class MarkBatchFragment extends LazyFragment implements MarkBatchChildContract.View {
	@ViewInject(R.id.loading)
	private ProgressBar progressBar;

	private Bitmap currentBitmap;

	private String picUrl;

	private GPUImageView mGPUImageView;

	@ViewInject(R.id.mBatch_view_container)
	private ViewGroup drawArea;

	private MarkBatchChildContract.Presenter markBatchChildPresenter;

	public static MarkBatchFragment newInstance(String imageUrl) {
		final MarkBatchFragment f = new MarkBatchFragment();
		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		f.setArguments(args);
		return f;
	}

	public static MarkBatchFragment newInstance(int position) {
		final MarkBatchFragment f = new MarkBatchFragment();
		final Bundle args = new Bundle();
		args.putInt("position", position);
		f.setArguments(args);
		return f;
	}

	@Override
	protected void initView(){
		markBatchChildPresenter = new MarkBatchChildPresenterImpl((BaseActivity) getActivity(),this);

	}


	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		isPrepared = true;
		lazyLoad();
	}

	private boolean isPrepared;

	@Override
	protected void lazyLoad() {
		if(!isPrepared || !isVisible) {
			return;
		}

		ViewGroup viewGroup = ((ViewGroup)overlayView.getParent());
		if(viewGroup != null) viewGroup.removeAllViews();

		int position = getArguments() != null ? getArguments().getInt("position") : 0;
		ImagePagerAdapter adapter = ((MarkBatchActivity)getActivity()).getImagePagerAdapter();
		List<Bitmap> bitmaps = adapter.getBitmaps();
		if(bitmaps.size() <= 0)
			adapter.imageLoader(picUrl);
		else
			adapter.imageLoader(position);
		mGPUImageView = adapter.getmGPUImageView();
		drawArea.addView(mGPUImageView);

		if(overlayView != null)
			drawArea.addView(overlayView);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        progressBar.setVisibility(View.VISIBLE);

		progressBar.setVisibility(View.GONE);
	}



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

	private View overlayView;

	public void setOverlayView(View overlayView) {
		this.overlayView = overlayView;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
}
