package com.zebra.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zebra.R;
import com.zebra.model.LabelModelImpl;
import com.zebra.util.BitmapUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/12/1.
 */

public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = LabelAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private RecyclerView mRecyclerView;
    private LabelAdapter.OnItemClickListener onItemClickListener;
    private List<LabelModelImpl> data;
    private int[] icon_entry;
    public LabelAdapter(Context context) {
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

    public LabelAdapter(Context context,List<LabelModelImpl> data) {
        this(context);
        this.data = data;
    }

    @Override
    public LabelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_mark_label, parent, false);
        itemView.setOnClickListener(this);
        return new LabelAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LabelAdapter.ViewHolder holder, int position) {
        if(position == 0) return;
        if(data != null) {
            holder.icon.setImageResource(data.get(position-1).getId());
            holder.label.setText(data.get(position-1).getContent());
        }
    }

    @Override
    public int getItemCount() {
        return this.data != null ? data.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @ViewInject(R.id.i_mark_iv)
        ImageView icon;

        @ViewInject(R.id.i_label)
        TextView label;

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
            if (parent.getChildPosition(view) == 0)
                outRect.left =  BitmapUtil.dip2px(x.app(),8);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    public void setOnItemClickListener(LabelAdapter.OnItemClickListener onItemClickListener) {
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
