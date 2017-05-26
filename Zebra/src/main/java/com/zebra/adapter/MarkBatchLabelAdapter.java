package com.zebra.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zebra.R;
import com.zebra.model.MarkBatchModelImpl;
import com.zebra.util.BitmapUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/29.
 */

public class MarkBatchLabelAdapter extends RecyclerView.Adapter<MarkBatchLabelAdapter.ViewHolder> implements View.OnClickListener{
    private static final String TAG = MarkBatchLabelAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private RecyclerView mRecyclerView;
    private OnItemClickListener onItemClickListener;
    private List<MarkBatchModelImpl> data;
    private int[] icon_entry;
    public MarkBatchLabelAdapter(Context context) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        icon_entry = new int[]{
                R.drawable.sticker1,
                R.drawable.sticker2,
                R.drawable.sticker3,
                R.drawable.sticker4,
                R.drawable.sticker5,
                R.drawable.sticker6,
                R.drawable.sticker7,
                R.drawable.sticker8};
    }

    public MarkBatchLabelAdapter(Context context, List<MarkBatchModelImpl> data) {
        this(context);
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_mark_label, parent, false);
        itemView.setOnClickListener(this);
        return new MarkBatchLabelAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(position == 0) return;
        if(data != null && data.size() != 0) {
//            holder.icon.setImageResource(data.get(position-1).getId());
            return;
        }
        holder.icon.setImageResource(icon_entry[position-1]);
    }

    @Override
    public int getItemCount() {
        return 9;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @ViewInject(R.id.i_mark_iv)
        ImageView icon;

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
            outRect.bottom = space;
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
}
