package com.zebra.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zebra.R;
import com.zebra.bean.PatternsMark;
import com.zebra.util.BitmapUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/29.
 */

public class MarkPosterAdapter extends RecyclerView.Adapter<MarkPosterAdapter.ViewHolder> implements View.OnClickListener{
    private static final String TAG = MarkPosterAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private RecyclerView mRecyclerView;
    private OnItemClickListener onItemClickListener;
    private List<PatternsMark> data;
    private int[] icon_entry;
    private Context mContext;
    public MarkPosterAdapter(Context context) {
        mContext = context;
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

    public MarkPosterAdapter(Context context, List<PatternsMark> data) {
        this(context);
        this.data = data;
    }

    public List<PatternsMark> getData() {
        return data;
    }

    public void setData(List<PatternsMark> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_poster_res, parent, false);
        itemView.setOnClickListener(this);
        return new MarkPosterAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(data != null) {
            PatternsMark model = data.get(position);
            int width = model.getThumwidth();
            int height = model.getThumheight();
            ViewGroup.LayoutParams params = holder.icon.getLayoutParams();
            if(width == height){
                params.height = BitmapUtil.dip2px(228/2.0f);
                params.width = BitmapUtil.dip2px(228/2.0f);
            }else {
                params.height = BitmapUtil.dip2px(414/2.0f);
                params.width = BitmapUtil.dip2px(228/2.0f);
            }
            holder.icon.setLayoutParams(params);
            x.image().bind(holder.icon,model.getImgthum());
        }

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @ViewInject(R.id.i_poster_res_iv)
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
            outRect.right = space;
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
            onItemClickListener.onItemClick(childAdapterPosition,v);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position,View view);
    }
}
