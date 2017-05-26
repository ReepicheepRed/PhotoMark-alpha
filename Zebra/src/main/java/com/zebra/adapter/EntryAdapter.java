package com.zebra.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zebra.R;
import com.zebra.util.BitmapUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by zhiPeng.S on 2016/11/18.
 */

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.ViewHolder> implements View.OnClickListener{
    private static final String TAG = EntryAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private RecyclerView mRecyclerView;
    private OnItemClickListener onItemClickListener;
    private int[] icon_entry;
    private String[] title_entry;

    public EntryAdapter(Context context) {
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        icon_entry = new int[]{
                R.drawable.paizhaoshuiyin_icon,
                R.drawable.piliangshuiyin_icon,
                R.drawable.jianjiepintu_icon,
                R.drawable.shangpinhaibao_icon};
        title_entry = context.getResources().getStringArray(R.array.entry_name);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_entry, parent, false);
        //导入itemView，为itemView设置点击事件
        itemView.setOnClickListener(this);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.icon.setImageResource(icon_entry[position]);
        holder.title.setText(title_entry[position]);
//        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
//        layoutParams.height=data.get(position).getHeight();
//        holder.itemView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return 4;
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
        Log.e(TAG, "onClick: "+childAdapterPosition );
        if (onItemClickListener!=null) {
            onItemClickListener.onItemClick(childAdapterPosition);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @ViewInject(R.id.entry_icon)
        ImageView icon;
        @ViewInject(R.id.entry_title)
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            x.view().inject(this, itemView);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
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
            outRect.right = space;
            outRect.bottom = space;

        }
    }
}
