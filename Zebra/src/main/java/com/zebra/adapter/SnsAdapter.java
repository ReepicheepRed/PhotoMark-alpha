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
import com.zebra.bean.Personal;
import com.zebra.util.BitmapUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/12/1.
 */

public class SnsAdapter extends RecyclerView.Adapter<SnsAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = SnsAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private RecyclerView mRecyclerView;
    private SnsAdapter.OnItemClickListener onItemClickListener;
    private List<Personal> data;
    private int[] icon;
    private String[] title;
    public SnsAdapter(Context context) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        icon = new int[]{
                R.drawable.xingming_icon,
                R.drawable.facebook_icon,
                R.drawable.line_icon,
                R.drawable.ins_icon,
                R.drawable.shangpinleibie_icon
        };
        title = context.getResources().getStringArray(R.array.sns_info);
    }

    public SnsAdapter(Context context, List<Personal> data) {
        this(context);
        this.data = data;
    }

    @Override
    public SnsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_sns, parent, false);
        itemView.setOnClickListener(this);
        return new SnsAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SnsAdapter.ViewHolder holder, int position) {
        holder.icon.setImageResource(icon[position]);
        holder.title.setText(title[position]);
        if(data != null) {
            holder.content.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return icon.length;
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        @ViewInject(R.id.sns_icon)
        ImageView icon;
        @ViewInject(R.id.sns_title)
        TextView title;
        @ViewInject(R.id.sns_content)
        TextView content;
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

    public void setOnItemClickListener(SnsAdapter.OnItemClickListener onItemClickListener) {
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
