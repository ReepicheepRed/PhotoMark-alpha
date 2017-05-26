package com.zebra.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zebra.R;
import com.zebra.WebActivity;
import com.zebra.bean.Banner;
import com.zebra.util.Constant;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class BannerAdapter extends ListBaseAdapter<Banner> {

	private LayoutInflater mInflater;
	private ImageOptions imageOptions;
	private BannerHolder holder;
	private int datasSize;

	public BannerAdapter(Context context, List<Banner> list) {
		super(context,list);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageOptions = new ImageOptions.Builder()
				.setLoadingDrawableId(R.drawable._banner)
				.setFailureDrawableId(R.drawable._banner)
				.build();
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;   //设置成最大值来无限循环
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_banner, null);
			holder = new BannerHolder();
			x.view().inject(holder, convertView);
			convertView.setTag(holder);
		}else {
			holder = (BannerHolder) convertView.getTag();
		}
        datasSize = getData().size();
		if(getData()==null || getData().size() == 0 || datasSize == 0){
			return  convertView;
		}

		final Banner model = getData().get(position % datasSize);


		String picUrl = getData().get(position%datasSize).getImg();
		x.image().bind(holder.bannerIv,picUrl,imageOptions);


		convertView.setOnClickListener(v -> {
			int fdi = model.getFdi();
            	switch(fdi){
                	case 1024:
						obtainDetailWebInfo(model.getLink());
						break;
            	}
        	});
		return convertView;
	}

	private class BannerHolder {
		@ViewInject(R.id.bannerIv)
		public ImageView bannerIv;
	}

	private void obtainDetailWebInfo(String url){
		Intent intent = new Intent(getContext(), WebActivity.class);
		intent.putExtra(Constant.WEB_H5,Constant.BANNER);
		intent.putExtra("link",url);
		getContext().startActivity(intent);
	}
}
