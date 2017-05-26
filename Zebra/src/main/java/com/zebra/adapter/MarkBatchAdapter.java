package com.zebra.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.sticker.app.model.Addon;
import com.zebra.R;
import com.zebra.util.BitmapUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/29.
 */

public class MarkBatchAdapter extends RecyclerView.Adapter<MarkBatchAdapter.ViewHolder> implements View.OnClickListener{
    private static final String TAG = MarkBatchAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private RecyclerView mRecyclerView;
    private OnItemClickListener onItemClickListener;
    private List<Addon> data;
    private int[] icon_entry;
    public MarkBatchAdapter(Context context) {
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

    public MarkBatchAdapter(Context context, List<Addon> data) {
        this(context);
        this.data = data;
    }

    public List<Addon> getData() {
        return data;
    }

    public void setData(List<Addon> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_mark_label, parent, false);
        itemView.setOnClickListener(this);
        return new MarkBatchAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(position == 0) {
            holder.icon.setImageResource(R.drawable.tab_btn_quanbu);
            return;
        }

        if(data != null && data.size() != 0) {
            Addon addon = data.get(position-1);
            Bitmap bitmap = BitmapFactory.decodeFile(addon.getImgPath());
            holder.icon.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 1 : data.size()+1;
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
