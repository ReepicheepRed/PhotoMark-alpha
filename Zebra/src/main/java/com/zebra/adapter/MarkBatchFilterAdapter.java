package com.zebra.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sticker.app.camera.effect.FilterEffect;
import com.sticker.app.camera.util.GPUImageFilterTools;
import com.zebra.R;
import com.zebra.util.BitmapUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * Created by zhiPeng.S on 2016/11/29.
 */

public class MarkBatchFilterAdapter extends RecyclerView.Adapter<MarkBatchFilterAdapter.ViewHolder> implements View.OnClickListener{
    private static final String TAG = MarkBatchFilterAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private RecyclerView mRecyclerView;
    private OnItemClickListener onItemClickListener;

    List<FilterEffect> data;
    Context mContext;
    private Bitmap background;
    private int  selectFilter = 0;
    public MarkBatchFilterAdapter(Context context, List<FilterEffect> effects, Bitmap background) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        data = effects;
        mContext = context;
        this.background = background;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_bottom_filter, parent, false);
        itemView.setOnClickListener(this);
        return new MarkBatchFilterAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final FilterEffect effect =  data.get(position);
//        Bitmap bitmap = holder.filteredImg.getGPUImage().getBitmapWithFilterApplied();
//        if(bitmap != null)
//            holder.filteredImg.setImage(bitmap);
//        else
        holder.filteredImg.setImage(background);
        GPUImageFilter filter = GPUImageFilterTools.createFilterForType(mContext, effect.getType());
        if(holder.filteredImg.getFilter() == null)
            holder.filteredImg.setFilter(filter);
        holder.filterName.setText(effect.getTitle());
        holder.filterName.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @ViewInject(R.id.small_filter)
        GPUImageView filteredImg;

        @ViewInject(R.id.filter_name)
        TextView filterName;

        private ViewHolder(View itemView) {
            super(itemView);
            x.view().inject(this, itemView);
        }
    }

    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = BitmapUtil.dip2px(x.app(),space);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        int childAdapterPosition = mRecyclerView.getChildAdapterPosition(v);
        if (onItemClickListener!=null) {
            onItemClickListener.onItemClick(childAdapterPosition);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public int getSelectFilter() {
        return selectFilter;
    }

    public void setSelectFilter(int selectFilter) {
        this.selectFilter = selectFilter;
    }
}
