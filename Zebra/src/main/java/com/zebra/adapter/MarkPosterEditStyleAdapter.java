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

public class MarkPosterEditStyleAdapter extends RecyclerView.Adapter<MarkPosterEditStyleAdapter.ViewHolder> implements View.OnClickListener{
    private static final String TAG = MarkPosterEditStyleAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private RecyclerView mRecyclerView;
    private OnItemClickListener onItemClickListener;
    private Context mContext;
    private boolean[] flag;
    private List<PatternsMark> data;
    public MarkPosterEditStyleAdapter(Context context, List<PatternsMark> data) {
        mContext = context;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setData(data);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_poster_template, parent, false);
        itemView.setOnClickListener(this);
        return new MarkPosterEditStyleAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(getItemCount() <= 0) return;

        PatternsMark patternsMark = data.get(position);

        x.image().bind(holder.style,patternsMark.getImgthum());
        if(flag[position])
            holder.token.setVisibility(View.VISIBLE);
        else
            holder.token.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public List<PatternsMark> getData() {
        return data;
    }

    public void setData(List<PatternsMark> data) {
        this.data = data;
        flag = new boolean[data.size()];
//        if(flag.length > 0) flag[0] = true;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @ViewInject(R.id.iPosterStyle_iv)
        ImageView style;

        @ViewInject(R.id.iPosterStyle_selected_iv)
        ImageView token;

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
            outRect.top = space;
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
            onItemClickListener.onItemClick(childAdapterPosition,v);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position, View view);
    }
}
